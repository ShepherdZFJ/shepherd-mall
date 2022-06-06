package com.shepherd.mallorder.feign;

import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallorder.dto.ProductSku;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/6/1 18:52
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductServiceTest {
    @Resource
    private ProductService productService;

    @Test
    public void getSku() {

        ResponseVO<ProductSku> sku = productService.getSku(1l);
        System.out.println(sku);

    }
}