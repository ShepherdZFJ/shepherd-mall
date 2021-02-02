package com.shepherd.mallproduct.api.controller;

import com.shepherd.mall.annotation.ResponseResultBody;
import com.shepherd.mallproduct.api.vo.BrandVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/2 18:50
 */
@RestController
@ResponseResultBody
@RequestMapping("/api/mall/brand")
@Api("品牌相关接口")
public class BrandController {

    @PostMapping
    @ApiOperation("添加品牌")
    public void addBrand (@RequestBody BrandVO brandVO) {

    }
}
