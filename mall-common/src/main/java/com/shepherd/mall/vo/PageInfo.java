package com.shepherd.mall.vo;

import lombok.Data;

import java.util.List;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/22 15:47
 */
@Data
public class PageInfo<T> {
    private int total;

    private int pageNo;

    private int pageSize;

    private int pages;

    private List<T> list;

    public PageInfo() {
    }

    public PageInfo(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PageInfo(Integer pageNo, Integer pageSize, int total, List<T> entityList) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.pageSize = entityList == null ? 0 : entityList.size();
        if (pageSize > 0) {
            // 计算分页数量
            int i = 0;
            if ((total % pageSize) > 0) {
                i = 1;
            }
            pages = total / pageSize + i;
        } else if (pageSize == 0) {
            pages = 1;
        } else {
            throw new RuntimeException(String.format("无法计算总页数，每页记录数不合法，size:", pageSize));
        }
        list = entityList;
    }
}

