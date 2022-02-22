package com.shepherd.mallproduct.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mall.vo.ResponseVO;
import com.shepherd.mallproduct.api.service.BrandService;
import com.shepherd.mallproduct.api.service.CategoryService;
import com.shepherd.mallproduct.api.service.ProductService;
import com.shepherd.mallproduct.constant.ProductConstant;
import com.shepherd.mallproduct.dao.ProductParamDAO;
import com.shepherd.mallproduct.dao.ProductSkuDAO;
import com.shepherd.mallproduct.dao.ProductSpecDAO;
import com.shepherd.mallproduct.dao.ProductSpuDAO;
import com.shepherd.mallproduct.dto.*;
import com.shepherd.mallproduct.entity.ProductParam;
import com.shepherd.mallproduct.entity.ProductSku;
import com.shepherd.mallproduct.entity.ProductSpec;
import com.shepherd.mallproduct.entity.ProductSpu;
import com.shepherd.mallproduct.feign.SearchService;
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
import java.util.Objects;
import java.util.concurrent.*;
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
    @Resource
    private SearchService searchService;

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("product-pool-%d").build();

    private static ExecutorService fixedThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2,
            Runtime.getRuntime().availableProcessors() * 40,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(Runtime.getRuntime().availableProcessors() * 20),
            namedThreadFactory);


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
        if (!CollectionUtils.isEmpty(productSkuList)) {
//            CategoryDTO categoryDTO = categoryService.getCategoryDetail(productSpu.getCategoryId());
//            BrandDTO brandDTO = brandService.getBrandDetail(productSpu.getBrandId());
            productSkuList.forEach(productSku -> {
                productSku.setSpuId(productSpu.getId());
                productSku.setBrandId(productSpu.getBrandId());
                //productSku.setBrandName(brandDTO == null ? null : brandDTO.getName());
                productSku.setCategoryId(productSpu.getCategoryId());
                //productSku.setCategoryName(categoryDTO == null ? null : categoryDTO.getName());
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
                    map.forEach((k, v) -> {
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
        updateWrapper.set("update_time", new Date());
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
        updateWrapper.in(ProductSku::getSpuId, productSpuIds);
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
        IPage<ProductSpu> page = productSpuDAO.selectPage(new Page<ProductSpu>(query.getPageNo(), query.getPageSize()), queryWrapper);
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

    @Override
    public List<ProductSku> getProductSku() {
        LambdaQueryWrapper<ProductSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductSku::getIsDelete, CommonConstant.NOT_DEL);
        List<ProductSku> productSkuList = productSkuDAO.selectList(queryWrapper);
        return productSkuList;
    }

    @Override
    public void upProductSpu(Long spuId) {
        LambdaQueryWrapper<ProductSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductSku::getSpuId, spuId);
        queryWrapper.eq(ProductSku::getIsDelete, CommonConstant.NOT_DEL);
        List<ProductSkuDTO> productSkuDTOS = productSkuDAO.selectList(queryWrapper).stream().map(productSku -> toProductSkuDTO(productSku)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(productSkuDTOS)) {
            List<Long> categoryIds = productSkuDTOS.stream().map(ProductSkuDTO::getCategoryId).collect(Collectors.toList());
            List<Long> brandIds = productSkuDTOS.stream().map(ProductSkuDTO::getBrandId).collect(Collectors.toList());
            List<CategoryDTO> categoryList = categoryService.getCategoryList(categoryIds);
            List<BrandDTO> brandList = brandService.getBrandList(brandIds);
            productSkuDTOS.forEach(productSkuDTO -> {
                String spec = productSkuDTO.getSpec();
                Map map = JSONObject.parseObject(spec, Map.class);
                productSkuDTO.setSpecMap(map);
                productSkuDTO.setHasStock(productSkuDTO.getStock() > 0);
                productSkuDTO.setHotScore(0l);
                CategoryDTO category = categoryList.stream().filter(categoryDTO -> Objects.equals(categoryDTO.getId(), productSkuDTO.getCategoryId())).findFirst().orElse(null);
                productSkuDTO.setCategoryName(category == null ? null : category.getName());
                BrandDTO brand = brandList.stream().filter(brandDTO -> Objects.equals(brandDTO.getId(), productSkuDTO.getBrandId())).findFirst().orElse(null);
                productSkuDTO.setBrandName(brand == null ? null : brand.getName());
                productSkuDTO.setBrandImg(brand == null ? null : brand.getImage());
            });
            ResponseVO responseVO = searchService.addProductToEs(productSkuDTOS);
            if (responseVO.getCode() == 200) {
                log.info("商品成功上架到es中了，可以修改商品的状态了");
                LambdaUpdateWrapper<ProductSpu> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(ProductSpu::getId, spuId);
                updateWrapper.eq(ProductSpu::getIsDelete, CommonConstant.NOT_DEL);
                updateWrapper.set(ProductSpu::getStatus, ProductConstant.PRODUCT_UP);
                int update = productSpuDAO.update(new ProductSpu(), updateWrapper);
            } else {
                //todo: 调用search服务失败处理机制，重试或者抛出异常;接口幂等性
            }

        }
    }

    /**
     * 使用completableFuture执行多线程任务安排，提高速度，completableFuture可以让某些异步线程任务串行化顺序执行
     * 如果不要求某些异步任务串行化顺序执行，那么也可以JUC里面另一个countDownLatch实现
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuDetail(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        CompletableFuture<ProductSku> skuFuture = CompletableFuture.supplyAsync(() -> {
            ProductSku sku = productSkuDAO.selectById(skuId);
            skuInfo.setSku(sku);
            return sku;
        }, fixedThreadPool);
        CompletableFuture<ProductSpu> spuFuture = skuFuture.thenApplyAsync(sku -> {
            ProductSpu spu = productSpuDAO.selectById(sku.getSpuId());
            skuInfo.setSpu(spu);
            return spu;
        }, fixedThreadPool);
        CompletableFuture<BrandDTO> brandFuture = skuFuture.thenApplyAsync(sku -> {
            BrandDTO brandDTO = brandService.getBrandDetail(sku.getBrandId());
            skuInfo.setBrand(brandDTO);
            return brandDTO;
        }, fixedThreadPool);
        CompletableFuture<CategoryDTO> categoryFuture = skuFuture.thenApplyAsync(sku -> {
            CategoryDTO categoryDTO = categoryService.getCategoryDetail(sku.getCategoryId());
            skuInfo.setCategory(categoryDTO);
            return categoryDTO;
        }, fixedThreadPool);
        try {
            CompletableFuture.allOf(skuFuture, spuFuture, brandFuture, categoryFuture).get();
        } catch (Exception e) {
            log.error("<=======等候所有任务执行过程报错：======>", e);
        }
        return skuInfo;
    }

    @Override
    public ProductSkuDTO getProductSku(Long skuId) {
        ProductSku productSku = productSkuDAO.selectById(skuId);
        return toProductSkuDTO(productSku);
    }

    @Override
    public List<ProductSkuDTO> getSkuPrice(List<Long> skuIds) {
        QueryWrapper<ProductSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "price");
        queryWrapper.in("id", skuIds);
        List<ProductSkuDTO> productSkuDTOList = productSkuDAO.selectList(queryWrapper).parallelStream().map(productSku -> toProductSkuDTO(productSku)).collect(Collectors.toList());
        return productSkuDTOList;
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
        queryWrapper.eq(ProductSku::getSpuId, productDTO.getProductSpuId());
        queryWrapper.eq(ProductSku::getIsDelete, CommonConstant.NOT_DEL);
        List<ProductSku> skuList = productSkuDAO.selectList(queryWrapper);
        productDTO.setSkuList(skuList);
        return productDTO;
    }

    private ProductSkuDTO toProductSkuDTO(ProductSku productSku) {
        if (productSku == null) {
            return null;
        }
        ProductSkuDTO productSkuDTO = MallBeanUtil.copy(productSku, ProductSkuDTO.class);
        productSkuDTO.setSkuId(productSku.getId());
        if (StringUtils.isNotBlank(productSku.getSpec())) {
            productSkuDTO.setSpecMap(JSON.parseObject(productSku.getSpec(), Map.class));
        }
        return productSkuDTO;
    }
}
