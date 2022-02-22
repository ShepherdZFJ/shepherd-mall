package com.shepherd.mall.seckill.service;

import com.oracle.tools.packager.Log;
import com.shepherd.mall.seckill.dto.SkuInfo;
import com.shepherd.mall.seckill.feign.ProductService;
import com.shepherd.mall.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/1 15:07
 */
@Component
@Slf4j
public class ProductFeignFallback implements ProductService {
    @Override
    public ResponseVO<SkuInfo> getSku(Long skuId) {
        log.info("执行feign接口对应的fallback方法了");
        ResponseVO responseVO = new ResponseVO();
        responseVO.setCode(200);
        responseVO.setMsg("success");
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setBrandName("自定义");
        skuInfo.setName("name");
        responseVO.setData(skuInfo);
        return responseVO;
    }
}
