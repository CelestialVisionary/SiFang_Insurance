package com.sifang.insurance.calculator.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 保费计算响应DTO
 */
@Data
public class PremiumCalculateResponse {
    
    /**
     * 计算结果状态：0-失败 1-成功
     */
    private Integer status;
    
    /**
     * 消息提示
     */
    private String message;
    
    /**
     * 年保费
     */
    private BigDecimal annualPremium;
    
    /**
     * 首期保费
     */
    private BigDecimal firstPayment;
    
    /**
     * 总保费
     */
    private BigDecimal totalPremium;
    
    /**
     * 基础费率
     */
    private BigDecimal baseRate;
    
    /**
     * 风险系数
     */
    private BigDecimal riskFactor;
    
    /**
     * 详细计算信息
     */
    private Map<String, Object> calculationDetails;
}