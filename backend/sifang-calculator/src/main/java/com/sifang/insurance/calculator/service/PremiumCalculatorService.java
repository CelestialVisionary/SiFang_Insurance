package com.sifang.insurance.calculator.service;

import com.sifang.insurance.calculator.entity.CalculationRecord;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 保费计算核心服务接口
 */
public interface PremiumCalculatorService {

    /**
     * 计算保费
     * @param productId 产品ID
     * @param userId 用户ID
     * @param params 计算参数
     * @return 计算记录
     */
    CalculationRecord calculatePremium(Long productId, String userId, Map<String, Object> params);

    /**
     * 根据规则ID计算保费
     * @param ruleId 规则ID
     * @param userId 用户ID
     * @param params 计算参数
     * @return 计算记录
     */
    CalculationRecord calculatePremiumByRule(Long ruleId, String userId, Map<String, Object> params);

    /**
     * 计算固定金额保费
     */
    BigDecimal calculateFixedAmount(Double baseAmount, Map<String, Object> params);

    /**
     * 计算比例保费
     */
    BigDecimal calculateRatePremium(Double baseRate, Double baseAmount, Map<String, Object> params);

    /**
     * 计算阶梯保费
     */
    BigDecimal calculateTieredPremium(String tierConfig, Double baseAmount, Map<String, Object> params);

    /**
     * 使用规则引擎计算保费
     */
    BigDecimal calculateWithRuleEngine(Long ruleId, Map<String, Object> params);

    /**
     * 应用折扣
     */
    BigDecimal applyDiscount(BigDecimal premium, Map<String, Object> discountInfo);

    /**
     * 应用附加费用
     */
    BigDecimal applyAdditionalFee(BigDecimal premium, Map<String, Object> additionalInfo);
}