package com.shepherd.mall.base;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/10/10 17:07
 */
@Data
public class BaseQuery {

    @Min(value = 1)
    private Integer pageSize;

    @Min(value = 1)
    private Integer pageNo;
}
