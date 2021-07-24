package com.shepherd.mallorder.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mall.utils.NumberUtil;
import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallorder.Constant.CartConstant;
import com.shepherd.mallorder.SkuQuery;
import com.shepherd.mallorder.api.service.CartService;
import com.shepherd.mallorder.api.vo.CartVO;
import com.shepherd.mallorder.dto.CartDTO;
import com.shepherd.mallorder.dto.CartItem;
import com.shepherd.mallorder.dto.ProductSku;
import com.shepherd.mallorder.feign.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/1 16:50
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ProductService productService;

    private static final String CART_PREFIX = "cart:";

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("cart-pool-%d").build();

    private static ExecutorService fixedThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()*2,
            Runtime.getRuntime().availableProcessors() * 40,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(Runtime.getRuntime().availableProcessors() * 20),
            namedThreadFactory);


    @Override
    public void addCartItem(Long skuId, Integer number, Long userId) {
        BoundHashOperations<String, Object, Object> cartHash = getCartHash(userId);
        String cartSkuInfo  = (String)cartHash.get(skuId);
        //如果没有就添加数据
        if (StringUtils.isBlank(cartSkuInfo)) {

            //2、添加新的商品到购物车(redis)
            CartItem cartItem = new CartItem();
            ResponseVO<ProductSku> responseVO = productService.getSku(skuId);
            ProductSku skuInfo = responseVO.getData();
            cartItem.setSkuId(skuInfo.getSkuId());
            cartItem.setName(skuInfo.getName());
            cartItem.setImage(skuInfo.getMainImage());
            cartItem.setPrice(skuInfo.getPrice());
            cartItem.setNumber(number);
            List<String> specValues = skuInfo.getSpecMap().values().stream().map(Objects::toString).collect(Collectors.toList());
            cartItem.setSpecValues(specValues);
            cartItem.setIsCheck(CartConstant.IS_CHECK);
            String cartItemJson = JSON.toJSONString(cartItem);
            cartHash.put(skuId, cartItemJson);
        } else {
            //购物车有此商品，修改数量即可
            CartItem cartItem = JSON.parseObject(cartSkuInfo, CartItem.class);
            cartItem.setNumber(cartItem.getNumber() + number);
            cartItem.setIsCheck(CartConstant.IS_CHECK);
            //修改redis的数据
            String cartItemJson = JSON.toJSONString(cartItem);
            cartHash.put(skuId.toString(), cartItemJson);
        }

    }

    @Override
    public void updateCartItem(CartItem cartItem, Long userId) {
        BoundHashOperations<String, Object, Object> cartHash = getCartHash(userId);
        Long skuId = cartItem.getSkuId();
        String skuInfo = (String)cartHash.get(skuId);
        CartItem item = JSON.parseObject(skuInfo, CartItem.class);
        if (cartItem.getIsCheck() != null) {
            item.setIsCheck(cartItem.getIsCheck());
        }
        if (cartItem.getNumber() != null) {
            item.setNumber(cartItem.getNumber());
        }
        cartHash.put(skuId, JSON.toJSONString(item));
    }

    @Override
    public void deleteCartItemBatch(List<Long> skuIds, Long userId) {
        if (CollectionUtils.isEmpty(skuIds)) {
            throw new BusinessException("删除的商品skuId集合不能为空");
        }
        BoundHashOperations<String, Object, Object> cartHash = getCartHash(userId);
        skuIds.forEach(skuId -> {
            cartHash.delete(skuId);
        });

    }

    @Override
    public CartDTO getCart(Long userId) {
        List<CartItem> cartItemList = getCartItemList(userId);
        CartDTO cart = new CartDTO();
        cart.setItems(cartItemList);
        Long countCheck = cartItemList.stream().filter(cartItem -> Objects.equals(cartItem.getIsCheck(), CartConstant.IS_CHECK)).count();
        cart.setCountCheck(countCheck.intValue());
        Integer countNumber = cartItemList.stream().map(CartItem::getNumber).reduce(0, Integer::sum);
        cart.setCountNum(countNumber);
        cartItemList.forEach(cartItem -> {
            BigDecimal sum = NumberUtil.safeMultiply(cartItem.getNumber(), cartItem.getPrice());
            cartItem.setTotalAmount(sum);
        });
        BigDecimal totalAmount = cartItemList.stream().map(CartItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(totalAmount);
        return cart;
    }

    @Override
    public List<CartItem> getCheckCartItemList(Long userId) {
        List<CartItem> cartItemList = getCartItemList(userId);
        List<CartItem> checkItemList = cartItemList.parallelStream().filter(cartItem -> Objects.equals(cartItem.getIsCheck(),
                CartConstant.IS_CHECK)).collect(Collectors.toList());
        List<Long> skuIds = checkItemList.parallelStream().map(CartItem::getSkuId).collect(Collectors.toList());
        SkuQuery query = new SkuQuery();
        query.setSkuIds(skuIds);
        ResponseVO<List<ProductSku>> responseVO = productService.getSkuPrice(query);
        List<ProductSku> skuList = responseVO.getData();
        cartItemList.forEach(cartItem -> {
            //设置最新价格
            ProductSku sku = skuList.parallelStream().filter(productSku -> Objects.equals(productSku.getSkuId(),
                    cartItem.getSkuId())).findFirst().orElse(null);
            if (sku != null) {
                cartItem.setPrice(sku.getPrice());
            }
        });
        return checkItemList;
    }

    private BoundHashOperations<String, Object, Object> getCartHash(Long userId) {
        String key = CART_PREFIX + userId;
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(key);
        return boundHashOperations;
    }

    private CartItem getCartItem(Long userId, Long skuId) {
        BoundHashOperations<String, Object, Object> cartHash = getCartHash(userId);
        String skuInfo = (String)cartHash.get(skuId);
        CartItem cartItem = JSON.parseObject(skuInfo, CartItem.class);
        return cartItem;
    }

    private List<CartItem> getCartItemList(Long userId) {
        //获取购物车里面的所有商品
        BoundHashOperations<String, Object, Object> cartHash = getCartHash(userId);
        List<Object> values = cartHash.values();
        if (!CollectionUtils.isEmpty(values)) {
            List<CartItem> cartItemList = values.stream().map((obj) -> JSON.parseObject((String) obj, CartItem.class))
                    .collect(Collectors.toList());
            return cartItemList;
        }
        return new ArrayList<>();
    }

}
