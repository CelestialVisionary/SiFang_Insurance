package com.sifang.insurance.calculator.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sifang.insurance.calculator.cache.CalculationCacheManager;
import com.sifang.insurance.calculator.constant.CalculatorConstants;
import com.sifang.insurance.calculator.entity.CalculationRecord;
import com.sifang.insurance.calculator.entity.CalculationRule;
import com.sifang.insurance.calculator.exception.CalculationException;
import com.sifang.insurance.calculator.service.CalculationRecordService;
import com.sifang.insurance.calculator.service.CalculationRuleService;
import com.sifang.insurance.calculator.service.PremiumCalculatorService;
import com.sifang.insurance.calculator.service.RuleEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 保费计算核心服务实现类
 */
@Service
public class PremiumCalculatorServiceImpl implements PremiumCalculatorService {

    @Autowired
    private CalculationRuleService calculationRuleService;

    @Autowired
    private CalculationRecordService calculationRecordService;

    @Autowired
    private RuleEngineService ruleEngineService;

    @Autowired
    private CalculationCacheManager cacheManager;

    @Value("${calculator.cache-enabled:true}")
    private boolean cacheEnabled;

    @Value("${calculator.cache-expire-time:3600}")
    private int cacheExpireTime;

    @Override
    public CalculationRecord calculatePremium(Long productId, String userId, Map<String, Object> params) {
        CalculationRecord record = new CalculationRecord();
        record.setCalculateNo(generateCalculateNo());
        record.setProductId(productId);
        record.setUserId(userId);
        record.setCalculateParams(JSON.toJSONString(params));

        try {
            // 获取产品启用的计算规则
            List<CalculationRule> rules = calculationRuleService.getEnabledRulesByProductId(productId);
            if (rules.isEmpty()) {
                throw new CalculationException(CalculatorConstants.ErrorCode.RULE_NOT_EXIST, "未找到有效的计算规则");
            }

            // 使用第一个规则进行计算（实际可能需要更复杂的规则选择逻辑）
            CalculationRule rule = rules.get(0);
            record.setRuleId(rule.getId());
            record.setProductName(rule.getRuleName());

            // 根据计算方式计算保费
            BigDecimal premium = calculateByMethod(rule, params);
            
            // 应用折扣和附加费用
            if (params.containsKey(CalculatorConstants.Param.DISCOUNT_INFO)) {
                premium = applyDiscount(premium, (Map<String, Object>) params.get(CalculatorConstants.Param.DISCOUNT_INFO));
            }
            
            if (params.containsKey(CalculatorConstants.Param.ADDITIONAL_INFO)) {
                premium = applyAdditionalFee(premium, (Map<String, Object>) params.get(CalculatorConstants.Param.ADDITIONAL_INFO));
            }
            
            record.setBasePremium(premium);
            record.setFinalPremium(premium);
            record.setCalculateDescription("计算成功");
            record.setStatus(CalculatorConstants.CalculationStatus.SUCCESS);

            // 缓存计算结果
            if (cacheEnabled) {
                cacheManager.cacheResult(record.getCalculateNo(), record, cacheExpireTime);
            }

        } catch (CalculationException e) {
            record.setStatus(CalculatorConstants.CalculationStatus.FAILURE);
            record.setFailReason(e.getMessage());
            record.setCalculateDescription("计算失败: " + e.getMessage());
        } catch (Exception e) {
            record.setStatus(CalculatorConstants.CalculationStatus.FAILURE);
            record.setFailReason(e.getMessage());
            record.setCalculateDescription("计算失败: " + e.getMessage());
        }

        // 保存计算记录
        calculationRecordService.saveCalculationRecord(record);
        return record;
    }

    @Override
    public CalculationRecord calculatePremiumByRule(Long ruleId, String userId, Map<String, Object> params) {
        CalculationRecord record = new CalculationRecord();
        record.setCalculateNo(generateCalculateNo());
        record.setUserId(userId);
        record.setCalculateParams(JSON.toJSONString(params));

        try {
            // 尝试从缓存获取规则
            CalculationRule rule;
            if (cacheEnabled) {
                rule = cacheManager.getRule(ruleId, CalculationRule.class);
                if (rule == null) {
                    rule = calculationRuleService.getById(ruleId);
                    if (rule != null) {
                        cacheManager.cacheRule(ruleId, rule, cacheExpireTime);
                    }
                }
            } else {
                rule = calculationRuleService.getById(ruleId);
            }

            if (rule == null) {
                throw new CalculationException(CalculatorConstants.ErrorCode.RULE_NOT_EXIST, "未找到指定的计算规则");
            }
            if (rule.getStatus() != CalculatorConstants.Status.ENABLED) {
                throw new CalculationException(CalculatorConstants.ErrorCode.RULE_NOT_ENABLED, "规则未启用");
            }

            record.setRuleId(rule.getId());
            record.setProductId(rule.getProductId());
            record.setProductName(rule.getRuleName());

            // 计算保费
            BigDecimal premium = calculateByMethod(rule, params);
            
            // 应用折扣和附加费用
            if (params.containsKey(CalculatorConstants.Param.DISCOUNT_INFO)) {
                premium = applyDiscount(premium, (Map<String, Object>) params.get(CalculatorConstants.Param.DISCOUNT_INFO));
            }
            
            if (params.containsKey(CalculatorConstants.Param.ADDITIONAL_INFO)) {
                premium = applyAdditionalFee(premium, (Map<String, Object>) params.get(CalculatorConstants.Param.ADDITIONAL_INFO));
            }
            
            record.setBasePremium(premium);
            record.setFinalPremium(premium);
            record.setCalculateDescription("计算成功");
            record.setStatus(CalculatorConstants.CalculationStatus.SUCCESS);

            // 缓存计算结果
            if (cacheEnabled) {
                cacheManager.cacheResult(record.getCalculateNo(), record, cacheExpireTime);
            }

        } catch (CalculationException e) {
            record.setStatus(CalculatorConstants.CalculationStatus.FAILURE);
            record.setFailReason(e.getMessage());
            record.setCalculateDescription("计算失败: " + e.getMessage());
        } catch (Exception e) {
            record.setStatus(CalculatorConstants.CalculationStatus.FAILURE);
            record.setFailReason(e.getMessage());
            record.setCalculateDescription("计算失败: " + e.getMessage());
        }

        // 保存计算记录
        calculationRecordService.saveCalculationRecord(record);
        return record;
    }

    /**
     * 根据计算方式计算保费
     */
    private BigDecimal calculateByMethod(CalculationRule rule, Map<String, Object> params) {
        BigDecimal premium;
        Integer method = rule.getCalculationMethod();

        if (method.equals(CalculatorConstants.CalculationMethod.FIXED_AMOUNT)) { // 固定金额
            premium = calculateFixedAmount(rule.getBaseRate(), params);
        } else if (method.equals(CalculatorConstants.CalculationMethod.RATE_CALCULATION)) { // 比例计算
            Double baseAmount = params.containsKey(CalculatorConstants.Param.AMOUNT) ? 
                    Double.valueOf(params.get(CalculatorConstants.Param.AMOUNT).toString()) : 0;
            premium = calculateRatePremium(rule.getBaseRate(), baseAmount, params);
        } else if (method.equals(CalculatorConstants.CalculationMethod.TIERED_CALCULATION)) { // 阶梯计算
            Double baseAmount = params.containsKey(CalculatorConstants.Param.AMOUNT) ? 
                    Double.valueOf(params.get(CalculatorConstants.Param.AMOUNT).toString()) : 0;
            premium = calculateTieredPremium(rule.getRuleContent(), baseAmount, params);
        } else if (method.equals(CalculatorConstants.CalculationMethod.RULE_ENGINE)) { // 规则引擎
            premium = calculateWithRuleEngine(rule.getId(), params);
        } else {
            throw new CalculationException(CalculatorConstants.ErrorCode.CALCULATION_FAILED, "不支持的计算方式");
        }

        // 应用最低/最高保费限制
        if (rule.getMinPremium() != null && premium.compareTo(BigDecimal.valueOf(rule.getMinPremium())) < 0) {
            premium = BigDecimal.valueOf(rule.getMinPremium());
        }
        if (rule.getMaxPremium() != null && premium.compareTo(BigDecimal.valueOf(rule.getMaxPremium())) > 0) {
            premium = BigDecimal.valueOf(rule.getMaxPremium());
        }

        return premium;
    }

    @Override
    public BigDecimal calculateFixedAmount(Double baseAmount, Map<String, Object> params) {
        return BigDecimal.valueOf(baseAmount != null ? baseAmount : 0);
    }

    @Override
    public BigDecimal calculateRatePremium(Double baseRate, Double baseAmount, Map<String, Object> params) {
        BigDecimal rate = BigDecimal.valueOf(baseRate != null ? baseRate : 0);
        BigDecimal amount = BigDecimal.valueOf(baseAmount != null ? baseAmount : 0);
        return amount.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public BigDecimal calculateTieredPremium(String tierConfig, Double baseAmount, Map<String, Object> params) {
        // 解析阶梯配置并计算
        try {
            // 使用FastJSON解析数组
            List<?> tierList = JSON.parseArray(tierConfig);
            List<Map<String, Object>> tiers = new ArrayList<>();
            for (Object tierObj : tierList) {
                if (tierObj instanceof Map) {
                    tiers.add((Map<String, Object>) tierObj);
                }
            }
            BigDecimal amount = BigDecimal.valueOf(baseAmount);
            BigDecimal premium = BigDecimal.ZERO;

            for (Map<String, Object> tier : tiers) {
                Double min = tier.containsKey("min") ? Double.valueOf(tier.get("min").toString()) : 0;
                Double max = tier.containsKey("max") ? Double.valueOf(tier.get("max").toString()) : Double.MAX_VALUE;
                Double rate = Double.valueOf(tier.get("rate").toString());

                if (amount.compareTo(BigDecimal.valueOf(min)) >= 0 && amount.compareTo(BigDecimal.valueOf(max)) <= 0) {
                    premium = amount.multiply(BigDecimal.valueOf(rate)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    break;
                }
            }

            if (premium.compareTo(BigDecimal.ZERO) == 0) {
                throw new CalculationException(CalculatorConstants.ErrorCode.CALCULATION_FAILED, "未找到匹配的阶梯配置");
            }

            return premium;
        } catch (CalculationException e) {
            throw e;
        } catch (Exception e) {
            throw new CalculationException(CalculatorConstants.ErrorCode.CALCULATION_FAILED, "阶梯配置解析失败: " + e.getMessage());
        }
    }

    @Override
    public BigDecimal calculateWithRuleEngine(Long ruleId, Map<String, Object> params) {
        try {
            // 使用规则引擎服务执行计算，传入null作为ruleCode参数
            Double premium = ruleEngineService.executeRule(ruleId, null, params);
            if (premium != null) {
                return BigDecimal.valueOf(premium);
            }
            throw new CalculationException(CalculatorConstants.ErrorCode.RULE_ENGINE_ERROR, "规则引擎计算结果无效");
        } catch (Exception e) {
            throw new CalculationException(CalculatorConstants.ErrorCode.RULE_ENGINE_ERROR, "规则引擎计算失败: " + e.getMessage());
        }
    }

    @Override
    public BigDecimal applyDiscount(BigDecimal premium, Map<String, Object> discountInfo) {
        if (discountInfo == null || !discountInfo.containsKey("discountRate")) {
            return premium;
        }

        try {
            Double discountRate = Double.valueOf(discountInfo.get("discountRate").toString());
            // 确保折扣率在有效范围内
            if (discountRate < 0 || discountRate > 100) {
                throw new CalculationException(CalculatorConstants.ErrorCode.PARAM_ERROR, "折扣率必须在0-100之间");
            }
            return premium.multiply(BigDecimal.valueOf(1 - discountRate / 100)).setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (CalculationException e) {
            throw e;
        } catch (Exception e) {
            throw new CalculationException(CalculatorConstants.ErrorCode.PARAM_ERROR, "折扣信息格式错误: " + e.getMessage());
        }
    }

    @Override
    public BigDecimal applyAdditionalFee(BigDecimal premium, Map<String, Object> additionalInfo) {
        if (additionalInfo == null || !additionalInfo.containsKey("additionalAmount")) {
            return premium;
        }

        try {
            Double additionalAmount = Double.valueOf(additionalInfo.get("additionalAmount").toString());
            // 确保附加费用非负
            if (additionalAmount < 0) {
                throw new CalculationException(CalculatorConstants.ErrorCode.PARAM_ERROR, "附加费用不能为负数");
            }
            return premium.add(BigDecimal.valueOf(additionalAmount)).setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (CalculationException e) {
            throw e;
        } catch (Exception e) {
            throw new CalculationException(CalculatorConstants.ErrorCode.PARAM_ERROR, "附加费用信息格式错误: " + e.getMessage());
        }
    }

    /**
     * 生成计算流水号
     */
    private String generateCalculateNo() {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "CALC" + date + uuid;
    }
}