package com.shepherd.mallproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.CategoryService;
import com.shepherd.mallproduct.api.service.ProductService;
import com.shepherd.mallproduct.dao.ProductDAO;
import com.shepherd.mallproduct.dto.ProductDTO;
import com.shepherd.mallproduct.entity.Product;
import com.shepherd.mallproduct.query.ProductQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
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
    private ProductDAO productDAO;
    @Resource
    private CategoryService categoryService;


    @Override
    public Long addProduct(ProductDTO productDTO) {
        Product product = MallBeanUtil.copy(productDTO, Product.class);
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        product.setIsDelete(CommonConstant.NOT_DEL);
        product.setStatus(CommonConstant.PRODUCT_ON_SALE);
        int insert = productDAO.insert(product);
        return product.getId();
    }

    @Override
    public Boolean delBatch(List<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return false;
        }
        UpdateWrapper<Product> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", productIds);
        updateWrapper.set("is_delete", CommonConstant.DEL);
        updateWrapper.set("update_time",new Date());
        int update = productDAO.update(new Product(), updateWrapper);
        return true;
    }

    @Override
    public Boolean updateProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            return false;
        }
        Product product = MallBeanUtil.copy(productDTO, Product.class);
        product.setUpdateTime(new Date());
        productDAO.updateById(product);

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
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        if (query.getCategoryId() != null) {
            List<Long> categoryIds = categoryService.getCategoryIds(query.getCategoryId());
            queryWrapper.in("category_id", categoryIds);
        }
        if (StringUtils.isNotBlank(query.getName())) {
            queryWrapper.like("name", query.getName());
        }
        queryWrapper.eq("is_delete", CommonConstant.NOT_DEL);
        IPage<Product> page = productDAO.selectPage(new Page<Product>(query.getPageNo(),query.getPageSize()), queryWrapper);
        List<Product> records = page.getRecords();
        Page<ProductDTO> dtoPage = new Page<>();
        dtoPage.setTotal(page.getTotal());
        dtoPage.setPages(page.getPages());
        dtoPage.setSize(page.getSize());
        dtoPage.setCurrent(page.getCurrent());
        dtoPage.setRecords(page.getRecords().stream().map(product -> toProductDTO(product)).collect(Collectors.toList()));
        return dtoPage;
    }

    @Override
    public ProductDTO getProductDetail(Long productId) {
        Product product = productDAO.selectById(productId);
        return MallBeanUtil.copy(product, ProductDTO.class);
    }


    private ProductDTO toProductDTO(Product product) {
        if (product == null) {
            return null;
        }
        ProductDTO productDTO = MallBeanUtil.copy(product, ProductDTO.class);
        productDTO.setProductId(product.getId());
        return productDTO;
    }
}
