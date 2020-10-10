package com.shepherd.mallproduct.dto;

import com.shepherd.mallproduct.entity.Product;
import lombok.Data;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 16:32
 */
@Data
public class ProductDTO extends Product {
    private Long productId;
}
