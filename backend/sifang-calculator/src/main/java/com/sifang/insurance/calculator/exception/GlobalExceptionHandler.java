package com.sifang.insurance.calculator.exception;

import com.sifang.insurance.calculator.constant.CalculatorConstants;
import com.sifang.insurance.common.entity.Result;
import com.sifang.insurance.common.entity.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        logger.warn("参数验证异常: {}", errors);
        Result<?> result = Result.fail(ResultCode.PARAM_ERROR.getCode(), "参数验证失败: " + errors);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<?>> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        logger.warn("参数绑定异常: {}", errors);
        Result<?> result = Result.fail(ResultCode.PARAM_ERROR.getCode(), "参数绑定失败: " + errors);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException ex) {
        logger.warn("业务异常: {}", ex.getMessage());
        Result<?> result = Result.fail(Integer.parseInt(ex.getCode()), ex.getMessage());
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理计算异常
     */
    @ExceptionHandler(CalculationException.class)
    public ResponseEntity<Result<?>> handleCalculationException(CalculationException ex) {
        logger.warn("计算异常: {}", ex.getMessage());
        Result<?> result = Result.fail(Integer.parseInt(ex.getErrorCode()), ex.getMessage());
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理规则引擎异常
     */
    @ExceptionHandler(RuleEngineException.class)
    public ResponseEntity<Result<?>> handleRuleEngineException(RuleEngineException ex) {
        logger.error("规则引擎异常: {}", ex.getMessage(), ex);
        Result<?> result = Result.fail(ResultCode.BUSINESS_ERROR.getCode(), ex.getMessage());
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Result<?>> handleNullPointerException(NullPointerException e) {
        logger.error("空指针异常: {}", e.getMessage(), e);
        Result<?> result = Result.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统错误: 空指针异常");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleGlobalException(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        Result<?> result = Result.fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统错误: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}