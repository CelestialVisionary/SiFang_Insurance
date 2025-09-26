package com.sifang.insurance.calculator.exception;

/**
 * 计算异常类
 */
public class CalculationException extends RuntimeException {

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 计算参数
     */
    private String calculationParams;

    public CalculationException(String message) {
        super(message);
    }

    public CalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CalculationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CalculationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public CalculationException(String errorCode, String message, String calculationParams) {
        super(message);
        this.errorCode = errorCode;
        this.calculationParams = calculationParams;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getCalculationParams() {
        return calculationParams;
    }

    public void setCalculationParams(String calculationParams) {
        this.calculationParams = calculationParams;
    }
}