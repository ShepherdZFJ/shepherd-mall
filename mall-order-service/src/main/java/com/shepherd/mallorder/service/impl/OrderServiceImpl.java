package com.shepherd.mallorder.service.impl;

import com.google.common.collect.Lists;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mall.utils.DateUtil;
import com.shepherd.mall.utils.IdWorker;
import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallorder.api.service.CartService;
import com.shepherd.mallorder.api.service.OrderService;
import com.shepherd.mallorder.dto.*;
import com.shepherd.mallorder.feign.UserService;
import com.shepherd.mallorder.feign.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/20 21:46
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Resource
    private ProductService productService;
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CartService cartService;
    @Resource
    private IdWorker idWorker;

    private static final String USER_ORDER_TOKEN_PREFIX="order:token:";


    @Override
    public OrderConfirmDTO settlement(Long userId) {
        OrderConfirmDTO orderConfirm = new OrderConfirmDTO();
       //1.获取收货地址信息
        ResponseVO<List<Address>> addressResponseVO = userService.getUserAddressList();
        List<Address> addressList = addressResponseVO.getData();
        orderConfirm.setAddressList(addressList);
        //2.获取购物车信息
        List<CartItem> checkCartItemList = cartService.getCheckCartItemList(userId);
       //3.获取个人积分
        ResponseVO<Integer> integerResponseVO = userService.getUserIntegration();
        Integer integration = integerResponseVO.getData();
        //4.获取优惠卷信息
        //todo

       //5.设置令牌，保证幂等性
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(USER_ORDER_TOKEN_PREFIX+userId, token, 10, TimeUnit.MINUTES);
        orderConfirm.setToken(token);
        return orderConfirm;

    }

    @Override
    public void submitOrder(OrderSubmitDTO orderSubmit) {
        String orderToken = orderSubmit.getToken();
        Long userId = orderSubmit.getUserId();
        //1.验证令牌，保证接口幂等性，防止重复提交订单，注意：【令牌的对比和删除必须保证原子性，否则高并发下会出现多次提交验证通过的情况】
//        String redisToken = stringRedisTemplate.opsForValue().get(USER_ORDER_TOKEN_PREFIX + userId);
//        if (Objects.equals(orderToken, redisToken)) {
//            stringRedisTemplate.delete(USER_ORDER_TOKEN_PREFIX + userId);
//        }
        String script= "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Integer result = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Integer.class), Lists.newArrayList(USER_ORDER_TOKEN_PREFIX + userId), orderToken);
        if (result < 1) {
            throw new BusinessException("订单令牌已过期或者不正确，请刷新重试提交");
        }
        //2.封装订单信息
        long l = idWorker.nextId();
        String currentDateStr = DateUtil.formatDate(new Date(), "yyyyMMddHHmmssSS");
        String orderNo = String.valueOf(l)+currentDateStr;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderNo(orderNo);
        ResponseVO<Address> addressResponseVO = userService.getAddressDetail(orderSubmit.getAddressId());
        Address address = addressResponseVO.getData();
        orderDTO.setReceiverName(address.getName());
        orderDTO.setReceiverMobile(address.getMobile());
        orderDTO.setReceiverProvince(address.getProvince());
        orderDTO.setReceiverCity(address.getCity());
        orderDTO.setReceiverRegion(address.getRegion());
        orderDTO.setReceiverDetailAddress(address.getDetailAddress());
        orderDTO.setOrderStatus(0);
        orderDTO.setDeliveryStatus(0);
        orderDTO.setPayStatus(0);











    }
}
