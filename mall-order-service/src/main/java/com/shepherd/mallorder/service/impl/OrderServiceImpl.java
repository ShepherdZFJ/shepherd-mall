package com.shepherd.mallorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mall.utils.DateUtil;
import com.shepherd.mall.utils.IdWorker;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallorder.Constant.OrderConstant;
import com.shepherd.mallorder.api.service.CartService;
import com.shepherd.mallorder.api.service.OrderItemService;
import com.shepherd.mallorder.api.service.OrderService;
import com.shepherd.mallorder.dao.OrderDAO;
import com.shepherd.mallorder.dao.OrderItemDAO;
import com.shepherd.mallorder.dto.*;
import com.shepherd.mallorder.entity.Order;
import com.shepherd.mallorder.entity.OrderItem;
import com.shepherd.mallorder.feign.UserService;
import com.shepherd.mallorder.feign.ProductService;
import com.shepherd.mallorder.feign.WareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/20 21:46
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDAO, Order> implements OrderService {
    @Resource
    private ProductService productService;
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CartService cartService;
    @Resource
    private WareService wareService;
    @Resource
    private IdWorker idWorker;
    @Resource
    private OrderDAO orderDAO;
    @Resource
    private OrderItemDAO orderItemDAO;
    @Resource
    private OrderItemService orderItemService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisTemplate redisTemplate;

    private static final String USER_ORDER_TOKEN_PREFIX = "order:token:";
    private static final String CART_PREFIX = "cart:";


    @Override
    public OrderConfirmDTO settlement(Long userId) {
        OrderConfirmDTO orderConfirm = new OrderConfirmDTO();
        //1.获取收货地址信息
        ResponseVO<List<Address>> addressResponseVO = userService.getUserAddressList();
        List<Address> addressList = addressResponseVO.getData();
        orderConfirm.setAddressList(addressList);
        //2.获取购物车信息
        List<CartItem> checkCartItemList = cartService.getCheckCartItemList(userId);
        orderConfirm.setOrderItemList(checkCartItemList);
        //3.获取个人积分
        ResponseVO<Integer> integerResponseVO = userService.getUserIntegration();
        Integer integration = integerResponseVO.getData();
        orderConfirm.setIntegration(integration);
        //4.获取优惠卷信息
        //todo

        //5.设置令牌，保证幂等性
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(USER_ORDER_TOKEN_PREFIX + userId, token, 30, TimeUnit.MINUTES);
        orderConfirm.setToken(token);
        return orderConfirm;

    }

    @Override
//    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(OrderSubmitDTO orderSubmit) {
        String orderToken = orderSubmit.getToken();
        Long userId = orderSubmit.getUserId();
        //1.验证令牌，保证接口幂等性，防止重复提交订单，注意：【令牌的对比和删除必须保证原子性，否则高并发下会出现多次提交验证通过的情况】
//        String redisToken = stringRedisTemplate.opsForValue().get(USER_ORDER_TOKEN_PREFIX + userId);
//        if (Objects.equals(orderToken, redisToken)) {
//            stringRedisTemplate.delete(USER_ORDER_TOKEN_PREFIX + userId);
//        }
//        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//        String s = stringRedisTemplate.opsForValue().get(USER_ORDER_TOKEN_PREFIX + userId);
//        Long result = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Lists.newArrayList(USER_ORDER_TOKEN_PREFIX + userId), orderToken);
//        if (result < 1) {
//            throw new BusinessException("订单令牌已过期或者不正确，请刷新重试提交");
//        }
        //2.封装订单信息
        long l = idWorker.nextId();
        String currentDateStr = DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSS");
        String orderNo = String.valueOf(l) + currentDateStr;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(orderSubmit.getUserId());
        orderDTO.setOrderNo(orderNo);
        ResponseVO<Address> addressResponseVO = userService.getAddressDetail(orderSubmit.getAddressId());
        Address address = addressResponseVO.getData();
        orderDTO.setReceiverName(address.getName());
        orderDTO.setReceiverMobile(address.getMobile());
        orderDTO.setReceiverProvince(address.getProvince());
        orderDTO.setReceiverCity(address.getCity());
        orderDTO.setReceiverRegion(address.getRegion());
        orderDTO.setReceiverDetailAddress(address.getDetailAddress());
        orderDTO.setStatus(OrderConstant.ORDER_STATUS_NEW);

        //3.封装订单明细信息
        List<CartItem> checkCartItemList = cartService.getCheckCartItemList(userId);
        List<OrderItem> orderItemList = new ArrayList<>();
        checkCartItemList.forEach(cartItem -> {
            OrderItem orderItem = MallBeanUtil.copy(cartItem, OrderItem.class);
            orderItem.setOrderNo(orderNo);
            orderItem.setSpec(JSONObject.toJSONString(cartItem.getSpecValues()));
            orderItem.setIsReturn(0);
            orderItemList.add(orderItem);
            //后续每个明细设置邮费，获取商品spu信息，获取相关优惠调整价格皆可以再次操作 todo
        });

        //4.结算订单总金额和应付金额
        BigDecimal totalAmount = new BigDecimal("0.00");
        BigDecimal payAmount = new BigDecimal("0.00");
        for (OrderItem orderItem : orderItemList) {
            totalAmount = totalAmount.add(orderItem.getTotalAmount());
            payAmount = payAmount.add(orderItem.getPayAmount());
        }
        orderDTO.setTotalAmount(totalAmount);
        orderDTO.setPayAmount(payAmount);

        //5.验证金额
        if (Math.abs(payAmount.subtract(orderSubmit.getPayAmount()).doubleValue()) > 0.01) {
            throw new BusinessException("传入的应付金额和后台计算的有误差，请核准在提交订单");
        }
        //6.保存订单
        orderDTO.setCreateTime(new Date());
        orderDTO.setUpdateTime(new Date());
        Order order = MallBeanUtil.copy(orderDTO, Order.class);
        int insert = orderDAO.insert(order);
        orderItemList.forEach(orderItem -> {
            orderItem.setOrderId(order.getId());
            orderItem.setCreateTime(new Date());
            orderItem.setUpdateTime(new Date());
        });
        orderItemService.saveBatch(orderItemList);



        //7.扣库存
        orderDTO.setOrderItemList(orderItemList);
        orderDTO.setOrderId(order.getId());
        try {
            ResponseVO responseVO = wareService.decreaseStock(orderDTO);
            if (Objects.equals(responseVO.getCode(), 200)) {
                //发送消息到订单延迟队列，方便后续自动关闭订单
                rabbitTemplate.convertAndSend("order-event-exchange","order.create.order", orderDTO);


            } else {
                throw new BusinessException("扣库存接口返回结果错误");
            }
        } catch (Exception e) {
            log.error("调用扣库存接口失败: ", e);
            throw new BusinessException("调用扣库存接口失败");
        }

//        Order test = null;
//        test.setOrderNo("111");
        //8.清除已下订单的购物车商品数据
//        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(CART_PREFIX + userId);
//        orderItemList.forEach(orderItem -> {
//            boundHashOperations.delete(orderItem.getSkuId());
//        });


        //9.完成其他任务，eg：增加积分，成长值，生成操作记录供大数据使用等 todo


    }

    @Override
    public void closeOrder(OrderDTO orderDTO) {
        Long orderId = orderDTO.getOrderId();
        Order order = orderDAO.selectById(orderId);
        //判断在投递消息之后到消息过期期间该订单状态有没有改变
        if (Objects.equals(order.getStatus(), OrderConstant.ORDER_STATUS_NEW)) {
            Order updateOrder = new Order();
            updateOrder.setId(orderId);
            updateOrder.setStatus(OrderConstant.ORDER_STATUS_CANCEL);
            updateOrder.setUpdateTime(new Date());
            updateOrder.setCloseTime(new Date());
            int i = orderDAO.updateById(updateOrder);
            //关单后发送消息通知其他服务进行关单相关的操作，如解锁库存
            rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", JSONObject.toJSONString(orderDTO));
        }


    }

    @Override
    public OrderDTO getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo, orderNo);
        queryWrapper.eq(Order::getIsDelete, CommonConstant.NOT_DEL);
        Order order = orderDAO.selectOne(queryWrapper);
        return toOrderDTO(order);
    }






    OrderDTO toOrderDTO(Order order) {
        if (order == null) {
            return null;
        }
        OrderDTO orderDTO = MallBeanUtil.copy(order, OrderDTO.class);
        orderDTO.setOrderId(order.getId());
        return orderDTO;
    }
}
