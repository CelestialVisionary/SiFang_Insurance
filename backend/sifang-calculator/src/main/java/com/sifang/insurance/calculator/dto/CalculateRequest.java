package com.sifang.insurance.calculator.dto;

import lombok.Data;

import java.util.Map;

/**
 * 保费计算请求DTO
 */
@Data
public class CalculateRequest {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 规则ID（可选，如果指定则使用特定规则）
     */
    private Long ruleId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 计算参数
     * 必须包含的参数根据计算方式不同而不同：
     * - 比例计算：baseAmount（基础金额）
     * - 阶梯计算：baseAmount（基础金额）
     * - 其他可能需要的参数：年龄、性别、保险期限等
     */
    private Map<String, Object> params;
}