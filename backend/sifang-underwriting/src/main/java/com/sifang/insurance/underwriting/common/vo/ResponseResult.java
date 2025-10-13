package com.sifang.insurance.underwriting.common.vo;

import lombok.Data;

/**
 * 通用响应结果类
 */
@Data
public class ResponseResult<T> {
    
    private Integer code;
    private String message;
    private T data;
    private long timestamp;
    
    public ResponseResult() {
        this.timestamp = System.currentTimeMillis();
    }
    
    private ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 成功响应，带数据
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "success", data);
    }
    
    /**
     * 成功响应，带数据和自定义消息
     */
    public static <T> ResponseResult<T> success(T data, String message) {
        return new ResponseResult<>(200, message, data);
    }
    
    /**
     * 失败响应，带错误消息
     */
    public static <T> ResponseResult<T> fail(String message) {
        return new ResponseResult<>(500, message, null);
    }
    
    /**
     * 失败响应，带自定义错误码和消息
     */
    public static <T> ResponseResult<T> fail(Integer code, String message) {
        return new ResponseResult<>(code, message, null);
    }
}