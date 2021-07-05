package com.shepherd.mallorder.api.controller;

import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallorder.api.service.CartService;
import com.shepherd.mallorder.api.vo.CartVO;
import com.shepherd.mallorder.dto.CartDTO;
import com.shepherd.mallorder.dto.CartItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/1 16:46
 */
@RestController
@Api(tags = "购物车相关接口")
@RequestMapping("/api/mall/cart")
public class CartController {
    @Resource
    private CartService cartService;

    @PostMapping
    @ApiOperation("添加商品到购物车")
    public void addCartItem(@RequestBody CartItem cartItem) {
        cartService.addCartItem(cartItem.getSkuId(), cartItem.getNumber(), 1l);
    }

    @PutMapping
    @ApiOperation("修改购物车商品")
    public void updateCartItem(@RequestBody CartItem cartItem) {
        cartService.updateCartItem(cartItem, 1l);
    }

    @DeleteMapping
    @ApiOperation("删除购物车商品(批量)")
    public void deleteCartItemBatch(@RequestBody  CartVO cartVO) {
        cartService.deleteCartItemBatch(cartVO.getSkuIds(), 1l);
    }

    @GetMapping
    @ApiOperation("获取购物车信息")
    public CartVO getCart() {
        CartDTO cartDTO = cartService.getCart(1l);
        CartVO cartVO = MallBeanUtil.copy(cartDTO, CartVO.class);
        return cartVO;
    }


}
