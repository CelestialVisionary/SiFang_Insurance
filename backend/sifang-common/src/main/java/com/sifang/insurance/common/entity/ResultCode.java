package com.sifang.insurance.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应结果状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 失败
     */
    FAIL(400, "操作失败"),
    
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),
    
    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    
    /**
     * 参数错误
     */
    PARAM_ERROR(40001, "参数错误"),
    
    /**
     * 业务逻辑错误
     */
    BUSINESS_ERROR(50001, "业务逻辑错误");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 状态消息
     */
    private final String message;
}