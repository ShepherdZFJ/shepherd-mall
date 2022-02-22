package com.shepherd.mallproduct.dto;

import com.shepherd.mallproduct.entity.Brand;
import lombok.Data;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2021/2/2 18:57
 */
@Data
public class BrandDTO extends Brand {
    private Long brandId;
}
