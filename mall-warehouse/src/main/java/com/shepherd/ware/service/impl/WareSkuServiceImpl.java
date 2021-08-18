package com.shepherd.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.enums.ResponseStatusEnum;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.ware.api.service.WareOrderTaskItemService;
import com.shepherd.ware.api.service.WareSkuService;
import com.shepherd.ware.constant.OrderConstant;
import com.shepherd.ware.dao.WareInfoDAO;
import com.shepherd.ware.dao.WareOrderTaskDAO;
import com.shepherd.ware.dao.WareSkuDAO;
import com.shepherd.ware.dto.*;
import com.shepherd.ware.entity.WareInfo;
import com.shepherd.ware.entity.WareOrderTask;
import com.shepherd.ware.entity.WareOrderTaskItem;
import com.shepherd.ware.entity.WareSku;
import com.shepherd.ware.feign.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 11:09
 */
@Service
@Slf4j
public class WareSkuServiceImpl implements WareSkuService {
    @Resource
    private WareSkuDAO wareSkuDAO;
    @Resource
    private WareInfoDAO wareInfoDAO;
    @Resource
    private WareOrderTaskDAO wareOrderTaskDAO;
    @Resource
    private WareOrderTaskItemService wareOrderTaskItemService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private OrderService orderService;




    @Override
    public WareSkuDTO getWareSkuDetail(Long wareId, Long skuId) {
        LambdaQueryWrapper<WareSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WareSku::getSkuId, skuId);
        queryWrapper.eq(WareSku::getWareId, wareId);
        queryWrapper.eq(WareSku::getIsDelete, CommonConstant.NOT_DEL);
        WareSku wareSku = wareSkuDAO.selectOne(queryWrapper);
        return toWareSkuDTO(wareSku);
    }

    @Override
    public List<WareInfoDTO> getHasStockWare(Long skuId, Integer number) {
        List<Long> wareIds = getWareIdsHasStock(skuId, number);
        LambdaQueryWrapper<WareInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(WareInfo::getId, wareIds);
        List<WareInfoDTO> wareInfoDTOS = wareInfoDAO.selectList(queryWrapper).parallelStream().map(wareInfo -> toWareInfoDTO(wareInfo)).collect(Collectors.toList());
        return wareInfoDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decreaseStock(Order order) {
        //1.根据订单生成仓库库存任务记录，防止订单回滚了，库存没有回滚，然后无记录可查的问题
        WareOrderTask wareOrderTask = WareOrderTask.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .status(1)
                .createTime(new Date())
                .updateTime(new Date()).build();
        wareOrderTaskDAO.insert(wareOrderTask);

        //2.扣减库存
        List<OrderItem> orderItemList = order.getOrderItemList();
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new BusinessException("订单明细不能为空");
        }
        List<WareOrderTaskItem> wareOrderTaskItems = new ArrayList<>();
        orderItemList.forEach(orderItem -> {
            Long skuId = orderItem.getSkuId();
            Integer number = orderItem.getNumber();
            //查出满足数量的商品仓库id集合
            List<Long> wareIds = getWareIdsHasStock(skuId, number);
            if (CollectionUtils.isEmpty(wareIds)) {
                throw new BusinessException("商品skuId："+skuId +"，库存补足");
            }
            Boolean flag = false;
            for (Long wareId : wareIds) {
                Integer update = wareSkuDAO.decreaseStock(wareId, skuId, number);
                if (update > 0) {
                    flag = true;
                    WareOrderTaskItem wareOrderTaskItem = WareOrderTaskItem.builder().wareId(wareId)
                            .taskId(wareOrderTask.getId())
                            .skuId(orderItem.getSkuId())
                            .skuName(orderItem.getSkuName())
                            .skuNum(orderItem.getNumber())
                            .status(1)
                            .isDelete(CommonConstant.NOT_DEL)
                            .createTime(new Date())
                            .updateTime(new Date()).build();
                    wareOrderTaskItems.add(wareOrderTaskItem);
                    break;
                }
            }
            if (!flag) {
                throw new BusinessException("商品skuId："+skuId +"，扣取库存失败");
            }
        });
        if (!CollectionUtils.isEmpty(wareOrderTaskItems)) {
            wareOrderTaskItemService.saveBatch(wareOrderTaskItems);
        }
        WareOrderTaskDTO wareOrderTaskDTO = MallBeanUtil.copy(wareOrderTask, WareOrderTaskDTO.class);
        wareOrderTaskDTO.setWareOrderTaskItems(wareOrderTaskItems);
        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", wareOrderTaskDTO);
    }

    @Override
    public void releaseStock(WareOrderTaskDTO wareOrderTaskDTO) {
        Long id = wareOrderTaskDTO.getId();
        WareOrderTask wareOrderTask = wareOrderTaskDAO.selectById(id);
        if (wareOrderTask == null || Objects.equals(wareOrderTask.getStatus(), 2)) {
            return;
        }

        String orderNo = wareOrderTaskDTO.getOrderNo();
        try {
            ResponseVO<Order> response = orderService.getOrderByOrderNo(orderNo);
            if (Objects.equals(response.getCode(), ResponseStatusEnum.SUCCESS.getCode())) {
                Order order = response.getData();
                //如果查询的订单为空，或者状态还是新建中(这种情况一般不会出现，因为订单的延时队列比库存的延时队列时间短，一般如果没付款状态会被更新为取消状态，但是以防止订单服务不能正常自动取消订单，我们可以先解锁库存
                if (order == null || Objects.equals(order.getStatus(), OrderConstant.ORDER_STATUS_CANCEL) || Objects.equals(order.getStatus(), OrderConstant.ORDER_STATUS_NEW)) {
                    WareOrderTask updateWareOrderTask = new WareOrderTask();
                    updateWareOrderTask.setId(id);
                    updateWareOrderTask.setStatus(2);
                    updateWareOrderTask.setUpdateTime(new Date());
                    wareOrderTaskDAO.updateById(updateWareOrderTask);
                    List<WareOrderTaskItem> wareOrderTaskItems = wareOrderTaskDTO.getWareOrderTaskItems();
                    List<WareOrderTaskItem> updateWareOrderTaskItemList = new ArrayList<>();
                    wareOrderTaskItems.forEach(wareOrderTaskItem -> {
                        //回滚库存, 防止重复回滚，先查一下这条明细当前状态, 保证幂等性
                        WareOrderTaskItem temp = wareOrderTaskItemService.getById(wareOrderTaskItem.getId());
                        if (Objects.equals(temp.getStatus(), 1)) {
                            wareSkuDAO.increaseStock(wareOrderTaskItem.getWareId(), wareOrderTaskItem.getSkuId(), wareOrderTaskItem.getSkuNum());
                            WareOrderTaskItem updateWareOrderTaskItem = new WareOrderTaskItem();
                            updateWareOrderTaskItem.setId(wareOrderTaskItem.getId());
                            updateWareOrderTaskItem.setStatus(2);
                            updateWareOrderTaskItem.setUpdateTime(new Date());
                            updateWareOrderTaskItemList.add(updateWareOrderTaskItem);
                        }

                    });
                    if (!CollectionUtils.isEmpty(updateWareOrderTaskItemList)) {
                        wareOrderTaskItemService.updateBatchById(updateWareOrderTaskItemList);
                    }

                } else {
                    throw new BusinessException("调用查询订单接口返回结果错误");
                }

            }
        } catch (Exception e) {
            log.error("调用查询订单接口返回结果错误", e);
            throw new BusinessException("调用查询订单接口失败");
        }

    }

    @Override
    public void releaseStock(Order order) {
        String orderNo = order.getOrderNo();
        LambdaQueryWrapper<WareOrderTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WareOrderTask::getOrderNo, orderNo);
        queryWrapper.eq(WareOrderTask::getIsDelete, CommonConstant.NOT_DEL);
        WareOrderTask wareOrderTask = wareOrderTaskDAO.selectOne(queryWrapper);
        if (wareOrderTask == null) {
            return ;
        }
        WareOrderTask updateWareOrderTask = new WareOrderTask();
        updateWareOrderTask.setId(wareOrderTask.getId());
        updateWareOrderTask.setStatus(2);
        updateWareOrderTask.setUpdateTime(new Date());
        wareOrderTaskDAO.updateById(updateWareOrderTask);
        List<WareOrderTaskItem> updateWareOrderTaskItemList = new ArrayList<>();
        LambdaQueryWrapper<WareOrderTaskItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(WareOrderTaskItem::getIsDelete, CommonConstant.NOT_DEL);
        lambdaQueryWrapper.eq(WareOrderTaskItem::getStatus, 1);
        lambdaQueryWrapper.eq(WareOrderTaskItem::getTaskId, wareOrderTask.getId());
        List<WareOrderTaskItem> wareOrderTaskItems = wareOrderTaskItemService.list(lambdaQueryWrapper);
        wareOrderTaskItems.forEach(wareOrderTaskItem -> {
            wareSkuDAO.increaseStock(wareOrderTaskItem.getWareId(), wareOrderTaskItem.getSkuId(), wareOrderTaskItem.getSkuNum());
            wareSkuDAO.increaseStock(wareOrderTaskItem.getWareId(), wareOrderTaskItem.getSkuId(), wareOrderTaskItem.getSkuNum());
            WareOrderTaskItem updateWareOrderTaskItem = new WareOrderTaskItem();
            updateWareOrderTaskItem.setId(wareOrderTaskItem.getId());
            updateWareOrderTaskItem.setStatus(2);
            updateWareOrderTaskItem.setUpdateTime(new Date());
            updateWareOrderTaskItemList.add(updateWareOrderTaskItem);
        });
        if (!CollectionUtils.isEmpty(updateWareOrderTaskItemList)) {
            wareOrderTaskItemService.updateBatchById(updateWareOrderTaskItemList);
        }


    }




    List<Long> getWareIdsHasStock(Long skuId, Integer number) {
        LambdaQueryWrapper<WareSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WareSku::getIsDelete, CommonConstant.NOT_DEL);
        queryWrapper.eq(WareSku::getSkuId, skuId);
        queryWrapper.ge(WareSku::getStock, number);
        List<Long> wareIds = wareSkuDAO.selectList(queryWrapper).parallelStream().map(WareSku::getWareId).collect(Collectors.toList());
        return wareIds;

    }

    WareSkuDTO toWareSkuDTO(WareSku wareSku) {
        if (wareSku == null) {
            return null;
        }
        WareSkuDTO wareSkuDTO = MallBeanUtil.copy(wareSku, WareSkuDTO.class);
        return wareSkuDTO;
    }

    WareInfoDTO toWareInfoDTO(WareInfo wareInfo) {
        if (wareInfo == null) {
            return null;
        }
        WareInfoDTO wareInfoDTO = MallBeanUtil.copy(wareInfo, WareInfoDTO.class);
        wareInfoDTO.setWareId(wareInfo.getId());
        return wareInfoDTO;
    }

}
