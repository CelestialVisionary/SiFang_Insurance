package com.sifang.insurance.calculator.util;

import com.sifang.insurance.common.entity.Result;
import com.sifang.insurance.common.entity.ResultCode;
import org.springframework.http.ResponseEntity;

/**
 * 响应工具类，用于统一响应格式
 */
public class ResponseUtil {

    /**
     * 成功响应
     */
    public static <T> ResponseEntity<Result<T>> success(T data) {
        return ResponseEntity.ok(Result.success(data));
    }

    /**
     * 成功响应，不带数据
     */
    public static ResponseEntity<Result<?>> success() {
        return ResponseEntity.ok(Result.success(null));
    }

    /**
     * 失败响应
     */
    public static ResponseEntity<Result<?>> fail(Integer code, String message) {
        return ResponseEntity.badRequest().body(Result.fail(code, message));
    }

    /**
     * 使用ResultCode的失败响应
     */
    public static ResponseEntity<Result<?>> fail(ResultCode resultCode) {
        return ResponseEntity.badRequest().body(Result.fail(resultCode));
    }

    /**
     * 使用ResultCode的失败响应，自定义消息
     */
    public static ResponseEntity<Result<?>> fail(ResultCode resultCode, String customMessage) {
        return ResponseEntity.badRequest().body(Result.fail(resultCode.getCode(), customMessage));
    }

    /**
     * 服务器内部错误响应
     */
    public static ResponseEntity<Result<?>> serverError(String message) {
        return ResponseEntity.status(500).body(Result.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message));
    }

    /**
     * 资源不存在响应
     */
    public static ResponseEntity<Result<?>> notFound(String message) {
        return ResponseEntity.ok(Result.fail(ResultCode.NOT_FOUND.getCode(), message));
    }

    /**
     * 未授权响应
     */
    public static ResponseEntity<Result<?>> unauthorized(String message) {
        return ResponseEntity.status(401).body(Result.fail(ResultCode.UNAUTHORIZED.getCode(), message));
    }
}