package com.sifang.insurance.calculator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 保费计算响应DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculateResponse {

    /**
     * 计算流水号
     */
    private String calculateNo;

    /**
     * 基础保费
     */
    private BigDecimal basePremium;

    /**
     * 最终保费
     */
    private BigDecimal finalPremium;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 附加费用
     */
    private BigDecimal additionalFee;

    /**
     * 计算结果说明
     */
    private String calculateDescription;

    /**
     * 计算状态：1-成功 2-失败
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 计算时间
     */
    private Date createTime;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 规则ID
     */
    private Long ruleId;
}