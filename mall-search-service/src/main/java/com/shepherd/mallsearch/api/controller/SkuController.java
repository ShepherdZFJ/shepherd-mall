package com.shepherd.mallsearch.api.controller;

import com.shepherd.mallsearch.api.service.SearchService;
import com.shepherd.mallsearch.dto.ProductSku;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/9 16:01
 */
@RestController
@RequestMapping("/api/mall/search")
@Api("搜索相关接口")
public class SkuController {

    @Resource
    private SearchService searchService;

    @GetMapping("/sku")
    public List<ProductSku> getSku() {
        return searchService.getSku();
    }
}
