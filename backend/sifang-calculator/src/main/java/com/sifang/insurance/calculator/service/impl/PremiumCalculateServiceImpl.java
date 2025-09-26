package com.sifang.insurance.calculator.service.impl;

import com.sifang.insurance.calculator.dto.CalculatePremiumRequest;
import com.sifang.insurance.calculator.dto.CalculatePremiumResponse;
import com.sifang.insurance.calculator.entity.PremiumRule;
import com.sifang.insurance.calculator.service.PremiumCalculateService;
import com.sifang.insurance.calculator.service.PremiumRuleService;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 保费计算服务实现类
 */
@Service
public class PremiumCalculateServiceImpl implements PremiumCalculateService {

    @Autowired
    private PremiumRuleService premiumRuleService;

    @Autowired
    private KieContainer kieContainer;

    @Override
    public CalculatePremiumResponse calculatePremium(CalculatePremiumRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 验证请求参数
        if (!validateRequest(request)) {
            throw new IllegalArgumentException("Invalid premium calculation request parameters");
        }
        
        // 使用Drools规则引擎计算保费
        CalculatePremiumResponse response = calculateWithDrools(request);
        
        // 设置计算耗时
        response.setCalculateTimeMillis(System.currentTimeMillis() - startTime);
        
        return response;
    }

    @Override
    public CalculatePremiumResponse calculateWithDrools(CalculatePremiumRequest request) {
        CalculatePremiumResponse response = new CalculatePremiumResponse();
        response.setProductId(request.getProductId());
        response.setEffectiveDate(request.getEffectiveDate());
        
        // 设置到期日期
        LocalDate expiryDate = request.getEffectiveDate().plusMonths(request.getCoveragePeriod());
        response.setExpiryDate(expiryDate);
        
        // 准备计算明细
        Map<String, Object> calculationDetails = new HashMap<>();
        response.setCalculationDetails(calculationDetails);
        
        // 获取产品规则
        List<PremiumRule> rules = premiumRuleService.getActiveRulesByProductId(request.getProductId());
        
        // 初始化基础保费和系数
        BigDecimal basePremium = BigDecimal.ZERO;
        BigDecimal ageFactor = BigDecimal.ONE;
        BigDecimal occupationFactor = BigDecimal.ONE;
        BigDecimal areaFactor = BigDecimal.ONE;
        
        // 计算基础保费和系数
        for (PremiumRule rule : rules) {
            switch (rule.getRuleType()) {
                case 1: // 基础费率
                    basePremium = rule.getRuleValue();
                    calculationDetails.put("baseRate", basePremium);
                    break;
                case 2: // 年龄系数
                    // 根据年龄范围应用不同的系数
                    if (isAgeMatch(request.getInsuredAge(), rule.getConditions())) {
                        ageFactor = rule.getRuleValue();
                        calculationDetails.put("ageFactor", ageFactor);
                    }
                    break;
                case 3: // 职业系数
                    // 根据职业应用不同的系数
                    if (isOccupationMatch(request.getInsuredOccupation(), rule.getConditions())) {
                        occupationFactor = rule.getRuleValue();
                        calculationDetails.put("occupationFactor", occupationFactor);
                    }
                    break;
                case 4: // 地区系数
                    // 根据地区应用不同的系数
                    if (isAreaMatch(request.getCoverageArea(), rule.getConditions())) {
                        areaFactor = rule.getRuleValue();
                        calculationDetails.put("areaFactor", areaFactor);
                    }
                    break;
            }
        }
        
        // 计算基础保费
        basePremium = request.getInsuredAmount().multiply(basePremium)
                .multiply(BigDecimal.valueOf(request.getCoveragePeriod() / 12.0));
        response.setBasePremium(basePremium);
        
        // 计算最终保费
        BigDecimal finalPremium = basePremium
                .multiply(ageFactor)
                .multiply(occupationFactor)
                .multiply(areaFactor)
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        response.setFinalPremium(finalPremium);
        
        // 记录系数组合结果
        calculationDetails.put("totalFactor", ageFactor.multiply(occupationFactor).multiply(areaFactor));
        
        return response;
    }
    
    @Override
    public boolean validateRequest(CalculatePremiumRequest request) {
        if (request == null) {
            return false;
        }
        if (request.getProductId() == null || request.getProductId() <= 0) {
            return false;
        }
        if (request.getInsuredAge() == null || request.getInsuredAge() < 0 || request.getInsuredAge() > 120) {
            return false;
        }
        if (request.getInsuredAmount() == null || request.getInsuredAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (request.getCoveragePeriod() == null || request.getCoveragePeriod() <= 0) {
            return false;
        }
        if (request.getEffectiveDate() == null || request.getEffectiveDate().isBefore(LocalDate.now())) {
            return false;
        }
        return true;
    }
    
    @Override
    public CalculatePremiumResponse getCalculateResultByNo(String calculateNo) {
        // 简单实现，实际应从数据库或缓存中查询
        return null;
    }
    
    @Override
    public Double getBaseRate(Long productId, Integer age, Integer gender) {
        // 获取产品的基础费率
        List<PremiumRule> rules = premiumRuleService.getActiveRulesByProductId(productId);
        for (PremiumRule rule : rules) {
            if (rule.getRuleType() == 1) { // 基础费率规则
                return rule.getRuleValue().doubleValue();
            }
        }
        return null;
    }
    
    /**
     * 判断年龄是否匹配规则条件
     */
    private boolean isAgeMatch(Integer age, String conditions) {
        // 简化实现，实际应该解析JSON格式的条件
        // 例如：{"min":18, "max":60}
        return true;
    }
    
    /**
     * 判断职业是否匹配规则条件
     */
    private boolean isOccupationMatch(String occupation, String conditions) {
        // 简化实现，实际应该解析JSON格式的条件
        return true;
    }
    
    /**
     * 判断地区是否匹配规则条件
     */
    private boolean isAreaMatch(String area, String conditions) {
        // 简化实现，实际应该解析JSON格式的条件
        return true;
    }
}