package com.sifang.insurance.calculator.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 保费计算请求DTO
 */
@Data
public class PremiumCalculateRequest {
    
    /**
     * 产品ID
     */
    private Long productId;
    
    /**
     * 被保险人年龄
     */
    private Integer age;
    
    /**
     * 被保险人性别 (1:男 2:女)
     */
    private Integer gender;
    
    /**
     * 保额
     */
    private BigDecimal sumInsured;
    
    /**
     * 保险期间（年）
     */
    private Integer insurancePeriod;
    
    /**
     * 缴费方式 (1:趸交 2:年缴 3:月缴)
     */
    private Integer paymentMethod;
    
    /**
     * 健康告知 (1:标准体 2:次标准体 3:拒保)
     */
    private Integer healthStatus;
    
    /**
     * 是否吸烟 (1:是 0:否)
     */
    private Integer isSmoker;
    
    /**
     * 职业类别
     */
    private String occupationType;
}