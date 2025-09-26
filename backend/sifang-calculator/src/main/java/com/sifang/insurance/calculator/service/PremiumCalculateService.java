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
}