package com.sifang.insurance.calculator.service;

import com.sifang.insurance.calculator.entity.CalculationRule;

import java.util.Map;

/**
 * 规则引擎服务接口
 */
public interface RuleEngineService {

    /**
     * 执行规则计算
     * @param ruleId 规则ID
     * @param ruleCode 规则编码
     * @param parameters 计算参数
     * @return 计算结果
     */
    Double executeRule(Long ruleId, String ruleCode, Map<String, Object> parameters);

    /**
     * 加载规则
     * @param rule 规则对象
     */
    void loadRule(CalculationRule rule);

    /**
     * 卸载规则
     * @param ruleId 规则ID
     * @param ruleCode 规则编码
     */
    void unloadRule(Long ruleId, String ruleCode);
}