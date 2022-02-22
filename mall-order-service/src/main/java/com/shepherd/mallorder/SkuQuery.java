package com.shepherd.mallorder;

import com.shepherd.mall.base.BaseQuery;
import lombok.Data;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/22 19:17
 */
@Data
public class SkuQuery extends BaseQuery {
    private List<Long> skuIds;
}
