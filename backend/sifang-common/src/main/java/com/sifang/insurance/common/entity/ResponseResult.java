package com.sifang.insurance.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应结果类
 */
@Data
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 构造方法
     */
    public ResponseResult() {
    }

    /**
     * 构造方法
     */
    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "success", data);
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(200, "success", null);
    }

    /**
     * 失败响应
     */
    public static <T> ResponseResult<T> fail(Integer code, String message) {
        return new ResponseResult<>(code, message, null);
    }

    /**
     * 失败响应（默认错误码）
     */
    public static <T> ResponseResult<T> fail(String message) {
        return new ResponseResult<>(500, message, null);
    }
}