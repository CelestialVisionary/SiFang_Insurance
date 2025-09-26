package com.sifang.insurance.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总条数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 分页数据
     */
    private List<T> list;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> build(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        int pages = (int) (total % pageSize == 0 ? total / pageSize : total / pageSize + 1);
        return new PageResult<>(pageNum, pageSize, total, pages, list);
    }
}