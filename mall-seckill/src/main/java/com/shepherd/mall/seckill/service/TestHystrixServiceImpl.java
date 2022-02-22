package com.shepherd.mall.seckill.service;

import com.alibaba.fastjson.JSONObject;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
//import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mall.seckill.api.service.TestHystrixService;
import com.shepherd.mall.seckill.dto.SkuInfo;
import com.shepherd.mall.seckill.feign.ProductService;
import com.shepherd.mall.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/1 10:33
 */
@Service
@Slf4j
public class TestHystrixServiceImpl implements TestHystrixService {
    @Resource
    private ProductService productService;
    @Resource
    private RestTemplate restTemplate;


    @Override
    //@HystrixCommand: fallbackMethod 熔断降级调的方法必须在同一个类中
//    @HystrixCommand(commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "50")},
//    fallbackMethod = "anotherBackMethod")
    public String testHystrix(Long skuId) {
        try {
            String result = restTemplate.getForObject("http://mall-product-service/api/mall/product/sku/" + skuId, String.class);
            return result;
        } catch (Exception e) {
            log.error("调用商品详情接口失败", e);
            throw new BusinessException("调用商品详情接口失败");
        }
    }

    @Override
    public String testFeignWithHystrix(Long skuId) {
        try {
            ResponseVO<SkuInfo> responseVO = productService.getSku(skuId);
            SkuInfo skuInfo = responseVO.getData();
            log.info("skuInfo: {}", skuInfo);
            return JSONObject.toJSONString(skuInfo);
        } catch (Exception e) {
            log.error("调用商品详情接口失败", e);
            throw new BusinessException("调用商品详情接口失败");
        }
    }

    public String backMethod(Long skuId){
        String s = "skuId"+skuId + "已经熔断降级了";
        return s;
    }
}
