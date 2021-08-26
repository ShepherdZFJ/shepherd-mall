package com.shepherd.mall.seckill.dto;

import com.shepherd.mall.seckill.entity.SeckillSession;
import com.shepherd.mall.seckill.entity.SeckillSku;
import lombok.Data;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/26 13:43
 */
@Data
public class SeckillSessionDTO extends SeckillSession {
    private List<SeckillSku> skuList;
}
