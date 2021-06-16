package com.shepherd.mallsearch.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.shepherd.mall.enums.ResponseStatusEnum;
import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallsearch.api.service.SearchService;
import com.shepherd.mallsearch.dto.ProductSku;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/9 16:01
 */
@RestController
@RefreshScope
@RequestMapping("/api/mall/search")
@Api("搜索相关接口")
public class SearchController {

    @Resource
    private SearchService searchService;

    @Value("${search.user.name}")
    String name;

    @Value("${search.user.age}")
    Integer age;

    @ApiOperation("批量上架商品到es中")
    @PostMapping("/product")
    public ResponseVO addProductToEsBatch(@RequestBody List<ProductSku> productSkuList) {
        Boolean result = searchService.addProductToEsBatch(productSkuList);
        if (result) {
            return ResponseVO.success();
        }
        return ResponseVO.failure(ResponseStatusEnum.BAD_REQUEST.getCode(), "商品上架发生异常");
    }

    @ApiOperation("测试nacos配置中心功能")
    @GetMapping("/nacos/config/test")
    public JSONObject testNacosConfig() {
        JSONObject rst = new JSONObject();
        rst.put("name", name);
        rst.put("age", age);
        return rst;
    }
}
