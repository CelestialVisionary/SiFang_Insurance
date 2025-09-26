package com.sifang.insurance.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 构造方法
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 构造方法（默认错误码）
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
    
    /**
     * 抛出业务异常
     */
    public static void throwException(String message) {
        throw new BusinessException(message);
    }
    
    /**
     * 抛出业务异常
     */
    public static void throwException(Integer code, String message) {
        throw new BusinessException(code, message);
    }
}