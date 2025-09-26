package com.sifang.insurance.calculator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * 保费计算响应DTO
 */
@Data
public class CalculatePremiumResponse {
    /**
     * 产品ID
     */
    private Long productId;
    
    /**
     * 产品名称
     */
    private String productName;
    
    /**
     * 基础保费
     */
    private BigDecimal basePremium;
    
    /**
     * 最终保费
     */
    private BigDecimal finalPremium;
    
    /**
     * 计算明细
     */
    private Map<String, Object> calculationDetails;
    
    /**
     * 起保日期
     */
    private LocalDate effectiveDate;
    
    /**
     * 到期日期
     */
    private LocalDate expiryDate;
    
    /**
     * 计算时间
     */
    private Long calculateTimeMillis;
}