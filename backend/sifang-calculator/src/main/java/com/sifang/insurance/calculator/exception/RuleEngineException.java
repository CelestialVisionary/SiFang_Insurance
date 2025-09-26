package com.sifang.insurance.calculator.exception;

/**
 * 规则引擎异常类
 */
public class RuleEngineException extends RuntimeException {

    /**
     * 规则ID
     */
    private Long ruleId;

    /**
     * 规则代码
     */
    private String ruleCode;

    public RuleEngineException(String message) {
        super(message);
    }

    public RuleEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuleEngineException(Long ruleId, String message) {
        super(message);
        this.ruleId = ruleId;
    }

    public RuleEngineException(String ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
    }

    public RuleEngineException(Long ruleId, String message, Throwable cause) {
        super(message, cause);
        this.ruleId = ruleId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }
}