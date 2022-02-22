package com.shepherd.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shepherd.ware.entity.WareSku;
import org.apache.ibatis.annotations.Param;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 10:53
 */
public interface WareSkuDAO  extends BaseMapper<WareSku> {


    Integer decreaseStock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("number") Integer number);

    Integer increaseStock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("number") Integer number);
}
