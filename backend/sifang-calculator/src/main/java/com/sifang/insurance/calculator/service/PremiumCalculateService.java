package com.sifang.insurance.calculator.service;

import com.sifang.insurance.calculator.dto.CalculatePremiumRequest;
import com.sifang.insurance.calculator.dto.CalculatePremiumResponse;

/**
 * 保费计算服务接口
 */
public interface PremiumCalculateService {
    
    /**
     * 计算保费
     */
    CalculatePremiumResponse calculatePremium(CalculatePremiumRequest request);
    
    /**
     * 使用Drools规则引擎计算保费
     */
    CalculatePremiumResponse calculateWithDrools(CalculatePremiumRequest request);
    
    /**
     * 校验保费计算参数
     */
    boolean validateRequest(CalculatePremiumRequest request);
    
    /**
     * 根据计算流水号查询计算结果
     */
    CalculatePremiumResponse getCalculateResultByNo(String calculateNo);
    
    /**
     * 获取产品的基础费率
     */
    Double getBaseRate(Long productId, Integer age, Integer gender);
}