package com.shepherd.ware.dto;

import com.shepherd.ware.entity.WareInfo;
import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/26 14:53
 */
@Data
public class WareInfoDTO extends WareInfo {
    private Long wareId;
}
