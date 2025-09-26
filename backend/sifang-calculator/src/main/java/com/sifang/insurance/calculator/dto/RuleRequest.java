package com.sifang.insurance.calculator.dto;

import lombok.Data;

/**
 * 规则管理请求DTO
 */
@Data
public class RuleRequest {

    /**
     * 规则ID（更新时使用）
     */
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品类型
     */
    private Integer productType;

    /**
     * 计算方式：1-固定金额 2-比例计算 3-阶梯计算 4-规则引擎
     */
    private Integer calculationMethod;

    /**
     * 基础费率
     */
    private Double baseRate;

    /**
     * 最低保费
     */
    private Double minPremium;

    /**
     * 最高保费
     */
    private Double maxPremium;

    /**
     * 规则内容（JSON格式，存储详细的计算参数）
     */
    private String ruleContent;

    /**
     * 规则状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}