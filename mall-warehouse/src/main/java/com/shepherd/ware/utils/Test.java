package com.shepherd.ware.utils;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/8/16 13:53
 */
@Data
@Builder
public class Test {
    private Long id;
    private String name;
    private String no;
    private Date date;
    private String uuid;
    private String str;

}
