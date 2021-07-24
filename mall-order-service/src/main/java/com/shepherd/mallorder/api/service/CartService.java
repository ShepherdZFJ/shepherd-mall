package com.shepherd.mallorder.api.service;

import com.shepherd.mallorder.dto.CartDTO;
import com.shepherd.mallorder.dto.CartItem;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/1 16:48
 */
public interface CartService {
    void addCartItem(Long skuId, Integer number, Long userId);

    void updateCartItem(CartItem cartItem, Long userId);

    void deleteCartItemBatch(List<Long> skuIds, Long userId);

    CartDTO getCart(Long userId);

    List<CartItem> getCheckCartItemList(Long userId);
}
