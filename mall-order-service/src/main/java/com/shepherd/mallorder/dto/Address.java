package com.shepherd.mallorder.dto;

import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/7/22 00:19
 */
@Data
public class Address {
    private Long id;
    private Long userId;
    private String name;
    private String mobile;
    private String postcode;
    private String province;
    private String city;
    private String region;
    private String detailAddress;
    private Integer isDefault;
    private Integer isDelete;
}
