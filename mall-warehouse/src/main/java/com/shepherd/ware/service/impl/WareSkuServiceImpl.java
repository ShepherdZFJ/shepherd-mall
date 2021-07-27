package com.shepherd.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.ware.api.service.WareSkuService;
import com.shepherd.ware.dao.WareInfoDAO;
import com.shepherd.ware.dao.WareSkuDAO;
import com.shepherd.ware.dto.Order;
import com.shepherd.ware.dto.OrderItem;
import com.shepherd.ware.dto.WareInfoDTO;
import com.shepherd.ware.dto.WareSkuDTO;
import com.shepherd.ware.entity.WareInfo;
import com.shepherd.ware.entity.WareSku;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
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
        //1.根据订单生成仓库库存任务记录，防止订单回滚了，库存没有回滚，然后无记录可查的问题 todo

        //2.扣减库存
        List<OrderItem> orderItemList = order.getOrderItemList();
        if (CollectionUtils.isEmpty(orderItemList)) {
            throw new BusinessException("订单明细不能为空");
        }
        orderItemList.forEach(orderItem -> {
            Long skuId = orderItem.getSkuId();
            Integer number = orderItem.getNumber();
            List<Long> wareIds = getWareIdsHasStock(skuId, number);
            if (CollectionUtils.isEmpty(wareIds)) {
                throw new BusinessException("商品skuId："+skuId +"，库存补足");
            }
            wareIds.forEach(wareId->{
                Integer update = wareSkuDAO.decreaseStock(wareId, skuId, number);
                if (update < 1) {
                    throw new BusinessException("商品skuId："+skuId +"，扣取库存失败");
                }
                //3.添加添加每个订单商品对应的仓库任务明细，方面后续排查数据不能对上等情况 todo

                //4.把扣库存的信息放入延迟队列中 todo
                return;

            });

        });

    }

    List<Long> getWareIdsHasStock(Long skuId, Integer number) {
        LambdaQueryWrapper<WareSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WareSku::getIsDelete, CommonConstant.NOT_DEL);
        queryWrapper.eq(WareSku::getSkuId, skuId);
        queryWrapper.ge(WareSku::getStock, number);
        List<Long> wareIds = wareSkuDAO.selectList(queryWrapper).parallelStream().map(WareSku::getId).collect(Collectors.toList());
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
