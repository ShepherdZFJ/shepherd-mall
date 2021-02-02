package com.shepherd.mallproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.CategoryService;
import com.shepherd.mallproduct.api.service.ProductService;
import com.shepherd.mallproduct.dao.ProductSkuDAO;
import com.shepherd.mallproduct.dao.ProductSpuDAO;
import com.shepherd.mallproduct.dto.ProductDTO;
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
    private CategoryService categoryService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addProduct(ProductDTO productDTO) {
        ProductSpu productSpu = MallBeanUtil.copy(productDTO, ProductSpu.class);
        productSpu.setCreateTime(new Date());
        productSpu.setUpdateTime(new Date());
        productSpu.setIsDelete(CommonConstant.NOT_DEL);
        productSpu.setStatus(CommonConstant.PRODUCT_ON_SALE);
        int insert = productSpuDAO.insert(productSpu);
        if (insert > 0) {
            if (!CollectionUtils.isEmpty(productDTO.getSkuList()))
            {
                productDTO.getSkuList().forEach(productSku -> {
                    productSku.setProductSpuId(productSpu.getId());
                    productSku.setCreateTime(new Date());
                    productSku.setUpdateTime(new Date());
                    productSku.setIsDelete(CommonConstant.NOT_DEL);
                    productSku.setStatus(CommonConstant.PRODUCT_ON_SALE);
                    productSkuDAO.insert(productSku);
                });
            }
        }
        return productSpu.getId();
    }

    @Override
    public Boolean delBatch(List<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return false;
        }
        UpdateWrapper<ProductSpu> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", productIds);
        updateWrapper.set("is_delete", CommonConstant.DEL);
        updateWrapper.set("update_time",new Date());
        int update = productSpuDAO.update(new ProductSpu(), updateWrapper);
        return true;
    }

    @Override
    public Boolean updateProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            return false;
        }
        ProductSpu productSpu = MallBeanUtil.copy(productDTO, ProductSpu.class);
        productSpu.setUpdateTime(new Date());
        productSpuDAO.updateById(productSpu);

        return true;
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
        return MallBeanUtil.copy(productSpu, ProductDTO.class);
    }


    private ProductDTO toProductDTO(ProductSpu productSpu) {
        if (productSpu == null) {
            return null;
        }
        ProductDTO productDTO = MallBeanUtil.copy(productSpu, ProductDTO.class);
        productDTO.setProductSpuId(productSpu.getId());
        return productDTO;
    }
}
