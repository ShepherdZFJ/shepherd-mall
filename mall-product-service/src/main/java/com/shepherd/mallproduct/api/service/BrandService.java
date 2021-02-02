package com.shepherd.mallproduct.api.service;

import com.shepherd.mallproduct.dto.BrandDTO;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/2 18:59
 */
public interface BrandService {

    void addBrand(BrandDTO brandDTO);

    List<BrandDTO> getBrandList(Long categoryId);
}
