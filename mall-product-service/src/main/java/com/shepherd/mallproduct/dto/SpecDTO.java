package com.shepherd.mallproduct.dto;

import lombok.Data;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/24 18:12
 */
@Data
public class SpecDTO {

    private String specName;
    private List<String> specValues;
}
