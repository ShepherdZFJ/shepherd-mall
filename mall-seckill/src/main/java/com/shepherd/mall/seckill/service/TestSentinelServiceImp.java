package com.shepherd.mall.seckill.service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import com.shepherd.mall.exception.BusinessException;
import com.shepherd.mall.seckill.api.service.TestSentinelService;
import com.shepherd.mall.seckill.dto.SkuInfo;
import com.shepherd.mall.seckill.feign.ProductService;
import com.shepherd.mall.vo.ResponseVO;
import com.sun.deploy.security.BlockedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/9/2 14:55
 */
@Service
@Slf4j
public class TestSentinelServiceImp implements TestSentinelService {
    @Resource
    private ProductService productService;


    @Override
    public String degrade(Long skuId) {
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

    @Override
    public String customResource() {
        log.info("执行测试自定义的保护资源咯");
        StringBuilder s = new StringBuilder();
        try(Entry entry = SphU.entry("customSource")) {
            for (int i =1; i<=5; i++) {
                s.append(i);
                TimeUnit.MILLISECONDS.sleep(100);
            }
            s.append("上山打老虎");
            return s.toString();
        } catch (BlockedException | BlockException | InterruptedException e) {
            log.error("自定义资源异常信息：{}", e);
            throw new BusinessException("自定义资源被限流了");
        }
    }

    @Override
    @SentinelResource(value = "annotationCustomResource", blockHandler = "blockHandler")
    public String annotationCustomResource() throws InterruptedException {

        StringBuilder s = new StringBuilder();
        for (int i =1; i<=5; i++) {
            s.append(i);
            TimeUnit.MILLISECONDS.sleep(10);
        }
        s.append("上山打老虎1111111");
        return s.toString();
    }

    public String blockHandler(BlockException e) {
        log.error("异常信息：{}", e);
        log.info("注解的降级方法执行了");
        return "block handler";
    }
}
