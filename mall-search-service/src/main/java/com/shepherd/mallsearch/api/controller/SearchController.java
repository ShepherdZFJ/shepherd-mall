package com.shepherd.mallsearch.api.controller;

import com.shepherd.mall.enums.ResponseStatusEnum;
import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallsearch.api.service.SearchService;
import com.shepherd.mallsearch.dto.ProductSku;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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
public class SearchController {

    @Resource
    private SearchService searchService;

    @ApiOperation("批量上架商品到es中")
    @PostMapping("/product")
    public ResponseVO addProductToEsBatch(@RequestBody List<ProductSku> productSkuList) {
        Boolean result = searchService.addProductToEsBatch(productSkuList);
        if (result) {
            return ResponseVO.success();
        }
        return ResponseVO.failure(ResponseStatusEnum.BAD_REQUEST.getCode(), "商品上架发生异常");
    }
}
