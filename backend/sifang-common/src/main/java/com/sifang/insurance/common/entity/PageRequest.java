package com.sifang.insurance.common.entity;

import lombok.Data;

/**
 * 分页查询请求基类
 */
@Data
public class PageRequest {
    /**
     * 页码，从1开始
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}