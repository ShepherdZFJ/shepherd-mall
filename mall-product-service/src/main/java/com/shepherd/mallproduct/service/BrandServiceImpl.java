package com.shepherd.mallproduct.service;

import com.shepherd.mallproduct.api.service.BrandService;
import com.shepherd.mallproduct.dao.BrandDAO;
import com.shepherd.mallproduct.dto.BrandDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
        brandDAO.insert(brandDTO);
    }
}
