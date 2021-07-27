package com.shepherd.ware.api.service;

import com.shepherd.ware.dto.Order;
import com.shepherd.ware.dto.WareInfoDTO;
import com.shepherd.ware.dto.WareSkuDTO;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 11:00
 */
public interface WareSkuService {

    WareSkuDTO getWareSkuDetail(Long wareId, Long skuId);

    List<WareInfoDTO> getHasStockWare(Long skuId, Integer number);

    void decreaseStock(Order order);
}
