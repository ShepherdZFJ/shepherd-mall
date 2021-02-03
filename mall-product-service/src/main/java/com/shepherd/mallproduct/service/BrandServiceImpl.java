package com.shepherd.mallproduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shepherd.mall.constant.CommonConstant;
import com.shepherd.mall.utils.MallBeanUtil;
import com.shepherd.mallproduct.api.service.BrandService;
import com.shepherd.mallproduct.dao.BrandDAO;
import com.shepherd.mallproduct.dto.BrandDTO;
import com.shepherd.mallproduct.entity.Brand;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/2 19:03
 */

@Service
public class BrandServiceImpl implements BrandService {
    @Resource
    private BrandDAO brandDAO;

    @Override
    public void addBrand(BrandDTO brandDTO) {
        brandDTO.setIsDelete(CommonConstant.NOT_DEL);
        brandDTO.setCreateTime(new Date());
        brandDTO.setUpdateTime(new Date());
        brandDAO.insert(brandDTO);
    }

    @Override
    public List<BrandDTO> getBrandList(Long categoryId) {
        LambdaQueryWrapper<Brand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Brand::getCategoryId, categoryId);
        queryWrapper.eq(Brand::getIsDelete, CommonConstant.NOT_DEL);
        List<BrandDTO> brandDTOList = brandDAO.selectList(queryWrapper).stream().map(brand -> toBrandDTO(brand)).collect(Collectors.toList());
        return brandDTOList;
    }

    @Override
    public BrandDTO getBrandDetail(Long brandId) {
        Brand brand = brandDAO.selectById(brandId);
        return toBrandDTO(brand);
    }

    BrandDTO toBrandDTO(Brand brand) {
        if (brand == null) {
            return null;
        }
        BrandDTO brandDTO = MallBeanUtil.copy(brand, BrandDTO.class);
        brandDTO.setBrandId(brand.getId());
        return brandDTO;
    }
}
