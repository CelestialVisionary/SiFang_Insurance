package com.sifang.insurance.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    /**
     * 产品ID
     */
    private Long productId;
    
    /**
     * 产品名称
     */
    private String productName;
    
    /**
     * 保费金额
     */
    private BigDecimal premiumAmount;
    
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
    private LocalDateTime effectiveDate;
    
    /**
     * 到期日期
     */
    private LocalDateTime expiryDate;
    
    /**
     * 订单备注
     */
    private String remark;
}