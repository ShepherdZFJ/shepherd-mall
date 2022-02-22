package com.shepherd.mallproduct.dto;

import com.shepherd.mallproduct.entity.ProductSpec;
import lombok.Data;

import java.util.List;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/3 11:51
 */
@Data
public class ProductSpecDTO extends ProductSpec {
    private Long productSpecId;
    private List<Long> productSpecIds;
}

