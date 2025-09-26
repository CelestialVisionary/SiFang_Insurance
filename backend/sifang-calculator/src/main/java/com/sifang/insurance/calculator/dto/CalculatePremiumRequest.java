package com.sifang.insurance.calculator.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 保费计算请求DTO
 */
@Data
public class CalculatePremiumRequest {
    /**
     * 产品ID
     */
    private Long productId;
    
    /**
     * 被保险人年龄
     */
    private Integer insuredAge;
    
    /**
     * 被保险人职业
     */
    private String insuredOccupation;
    
    /**
     * 保障地区
     */
    private String coverageArea;
    
    /**
     * 保险金额
     */
    private BigDecimal insuredAmount;
    
    /**
     * 保障期限（月）
     */
    private Integer coveragePeriod;
    
    /**
     * 起保日期
     */
    private LocalDate effectiveDate;
    
    /**
     * 附加信息
     */
    private String additionalInfo;
}