package com.shepherd.mallproduct.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.BrandService;
import com.shepherd.mallproduct.api.service.CategoryService;
import com.shepherd.mallproduct.api.service.ProductService;
import com.shepherd.mallproduct.dao.ProductParamDAO;
import com.shepherd.mallproduct.dao.ProductSkuDAO;
import com.shepherd.mallproduct.dao.ProductSpecDAO;
import com.shepherd.mallproduct.dao.ProductSpuDAO;
import com.shepherd.mallproduct.dto.*;
import com.shepherd.mallproduct.entity.ProductParam;
import com.shepherd.mallproduct.entity.ProductSku;
import com.shepherd.mallproduct.entity.ProductSpec;
import com.shepherd.mallproduct.entity.ProductSpu;
import com.shepherd.mallproduct.query.ProductQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/12 18:55
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductSpuDAO productSpuDAO;
    @Resource
    private ProductSkuDAO productSkuDAO;
    @Resource
    private ProductSpecDAO productSpecDAO;
    @Resource
    private ProductParamDAO productParamDAO;
    @Resource
    private CategoryService categoryService;
    @Resource
    private BrandService brandService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addProduct(ProductDTO productDTO) {
        //添加商品spu
        ProductSpu productSpu = MallBeanUtil.copy(productDTO, ProductSpu.class);
        productSpu.setCreateTime(new Date());
        productSpu.setUpdateTime(new Date());
        productSpu.setIsDelete(CommonConstant.NOT_DEL);
        productSpu.setStatus(CommonConstant.PRODUCT_ON_SALE);
        productSpu.setSaleCount(CommonConstant.DEFAULT_SALE_COUNT);
        int insert = productSpuDAO.insert(productSpu);
        if (insert > 0) {
           addProductSku(productSpu, productDTO.getSkuList());
        }
        return productSpu.getId();
    }

    Long addProductSpu(ProductSpu productSpu) {
        productSpu.setCreateTime(new Date());
        productSpu.setUpdateTime(new Date());
        productSpu.setIsDelete(CommonConstant.NOT_DEL);
        productSpu.setStatus(CommonConstant.PRODUCT_ON_SALE);
        productSpu.setSaleCount(CommonConstant.DEFAULT_SALE_COUNT);
        int insert = productSpuDAO.insert(productSpu);
        return productSpu.getId();
    }

    void addProductSku(ProductSpu productSpu, List<ProductSku> productSkuList) {
        if (!CollectionUtils.isEmpty(productSkuList))
        {
            CategoryDTO categoryDTO = categoryService.getCategoryDetail(productSpu.getCategoryId());
            BrandDTO brandDTO = brandService.getBrandDetail(productSpu.getBrandId());
            productSkuList.forEach(productSku -> {
                productSku.setProductSpuId(productSpu.getId());
                productSku.setBrandId(productSpu.getBrandId());
                productSku.setBrandName(brandDTO == null ? null : brandDTO.getName());
                productSku.setCategoryId(productSpu.getCategoryId());
                productSku.setCategoryName(categoryDTO == null ? null : categoryDTO.getName());
                productSku.setCreateTime(new Date());
                productSku.setUpdateTime(new Date());
                productSku.setIsDelete(CommonConstant.NOT_DEL);
                productSku.setStatus(CommonConstant.PRODUCT_ON_SALE);
                productSku.setSaleCount(CommonConstant.DEFAULT_SALE_COUNT);

                String name = productSpu.getName();
                StringBuilder skuName = new StringBuilder(name);
                String spec = productSku.getSpec(); //{ 颜色:红色,内存大小:16G}
                if (StringUtils.isNotBlank(spec)) {
                    Map<String, String> map = JSON.parseObject(spec, Map.class);
                    // 转成map对象  key:颜色  value:红色
//                        for (String key : map.keySet()) {
//                            // 获取SPU的名称 拼接 即可
//                            name += " " + map.get(key);
//                        }
                    map.forEach((k,v) ->{
                        skuName.append(" ").append(v);
                    });
                }
                productSku.setName(skuName.toString());
                productSkuDAO.insert(productSku);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delBatch(List<Long> productSpuIds) {
        if (CollectionUtils.isEmpty(productSpuIds)) {
            return false;
        }
        UpdateWrapper<ProductSpu> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", productSpuIds);
        updateWrapper.set("is_delete", CommonConstant.DEL);
        updateWrapper.set("update_time",new Date());
        int update = productSpuDAO.update(new ProductSpu(), updateWrapper);
        if (update > 0) {
            delProductSku(productSpuIds);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            return false;
        }
        ProductSpu productSpu = MallBeanUtil.copy(productDTO, ProductSpu.class);
        if (productSpu.getId() == null) {
            productSpu.setId(productDTO.getProductSpuId());
        }
        productSpu.setUpdateTime(new Date());
        int update = productSpuDAO.updateById(productSpu);
        if (update > 0) {
            //先删除商品的sku
            delProductSku(Lists.newArrayList(productSpu.getId()));
            //再添加商品的sku
            addProductSku(productSpu, productDTO.getSkuList());
        }
        return true;
    }

    void delProductSku(List<Long> productSpuIds) {
        LambdaUpdateWrapper<ProductSku> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ProductSku::getProductSpuId, productSpuIds);
        updateWrapper.eq(ProductSku::getIsDelete, CommonConstant.NOT_DEL);
        updateWrapper.set(ProductSku::getIsDelete, CommonConstant.DEL);
        updateWrapper.set(ProductSku::getUpdateTime, new Date());
        productSkuDAO.update(new ProductSku(), updateWrapper);
    }
    @Override
    public IPage<ProductDTO> getProductList(ProductQuery query) {
        if (query.getPageNo() == null) {
            query.setPageNo(CommonConstant.DEFAULT_PAGE_NO);
        }
        if (query.getPageSize() == null) {
            query.setPageSize(CommonConstant.DEFAULT_PAGE_SIZE);
        }
        QueryWrapper<ProductSpu> queryWrapper = new QueryWrapper<>();
        if (query.getCategoryId() != null) {
            List<Long> categoryIds = categoryService.getCategoryIds(query.getCategoryId());
            queryWrapper.in("category_id", categoryIds);
        }
        if (StringUtils.isNotBlank(query.getName())) {
            queryWrapper.like("name", query.getName());
        }
        queryWrapper.eq("is_delete", CommonConstant.NOT_DEL);
        IPage<ProductSpu> page = productSpuDAO.selectPage(new Page<ProductSpu>(query.getPageNo(),query.getPageSize()), queryWrapper);
        List<ProductSpu> records = page.getRecords();
        Page<ProductDTO> dtoPage = new Page<>();
        dtoPage.setTotal(page.getTotal());
        dtoPage.setPages(page.getPages());
        dtoPage.setSize(page.getSize());
        dtoPage.setCurrent(page.getCurrent());
        dtoPage.setRecords(page.getRecords().stream().map(productSpu -> toProductDTO(productSpu)).collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public ProductDTO getProductDetail(Long productId) {
        ProductSpu productSpu = productSpuDAO.selectById(productId);
        return toProductDTO(productSpu);
    }

    @Override
    public List<ProductSpecDTO> getProductSpecList(Long categoryId) {
        LambdaQueryWrapper<ProductSpec> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductSpec::getCategoryId, categoryId);
        queryWrapper.eq(ProductSpec::getIsDelete, CommonConstant.NOT_DEL);
        List<ProductSpecDTO> productSpecDTOList = productSpecDAO.selectList(queryWrapper).stream().map(productSpec -> toProductSpecDTO(productSpec)).collect(Collectors.toList());
        return productSpecDTOList;
    }

    @Override
    public List<ProductParamDTO> getProductParamList(Long categoryId) {
        LambdaQueryWrapper<ProductParam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductParam::getCategoryId, categoryId);
        queryWrapper.eq(ProductParam::getIsDelete, CommonConstant.NOT_DEL);
        List<ProductParamDTO> productParamDTOList = productParamDAO.selectList(queryWrapper).stream().map(productParam -> toProductParamDTO(productParam)).collect(Collectors.toList());
        return productParamDTOList;
    }

    private ProductSpecDTO toProductSpecDTO(ProductSpec productSpec) {
        if (productSpec == null) {
            return null;
        }
        ProductSpecDTO productSpecDTO = MallBeanUtil.copy(productSpec, ProductSpecDTO.class);
        productSpecDTO.setProductSpecId(productSpec.getId());
        return productSpecDTO;
    }

    private ProductParamDTO toProductParamDTO(ProductParam productParam) {
        if (productParam == null) {
            return null;
        }
        ProductParamDTO productParamDTO = MallBeanUtil.copy(productParam, ProductParamDTO.class);
        productParamDTO.setProductParamId(productParam.getId());
        return productParamDTO;
    }


    private ProductDTO toProductDTO(ProductSpu productSpu) {
        if (productSpu == null) {
            return null;
        }
        ProductDTO productDTO = MallBeanUtil.copy(productSpu, ProductDTO.class);
        productDTO.setProductSpuId(productSpu.getId());
        LambdaQueryWrapper<ProductSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductSku::getProductSpuId, productDTO.getProductSpuId());
        queryWrapper.eq(ProductSku::getIsDelete, CommonConstant.NOT_DEL);
        List<ProductSku> skuList = productSkuDAO.selectList(queryWrapper);
        productDTO.setSkuList(skuList);
        return productDTO;
    }
}
