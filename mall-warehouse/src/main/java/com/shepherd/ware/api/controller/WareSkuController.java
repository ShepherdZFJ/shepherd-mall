package com.shepherd.ware.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.ware.api.service.WareSkuService;
import com.shepherd.ware.dto.Order;
import com.shepherd.ware.dto.WareSkuDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 10:56
 */
@RestController
@RequestMapping("/api/mall/ware/sku")
@Api(tags = "仓库相关接口")
@ResponseResultBody
public class WareSkuController {
    @Resource
    private WareSkuService wareSkuService;

    @GetMapping()
    @ApiOperation("查询仓库sku商品详情")
    public WareSkuDTO getWareSkuDetail(@RequestParam("wareId") Long wareId, @RequestParam("skuId") Long skuId) {
        WareSkuDTO wareSkuDTO = wareSkuService.getWareSkuDetail(wareId, skuId);
        return wareSkuDTO;
    }

    @PostMapping("/stock/decrease")
    public void decreaseStock(@RequestBody Order order) {
        wareSkuService.decreaseStock(order);
    }
}
