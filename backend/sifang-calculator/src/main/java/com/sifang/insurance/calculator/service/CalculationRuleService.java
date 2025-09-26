package com.sifang.insurance.calculator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sifang.insurance.calculator.entity.CalculationRule;

import java.util.List;

/**
 * 计算规则服务接口
 */
public interface CalculationRuleService extends IService<CalculationRule> {

    /**
     * 根据产品ID获取启用的计算规则
     */
    List<CalculationRule> getEnabledRulesByProductId(Long productId);

    /**
     * 根据规则编码获取规则
     */
    CalculationRule getByRuleCode(String ruleCode);

    /**
     * 根据产品类型获取规则列表
     */
    List<CalculationRule> getRulesByProductType(Integer productType);

    /**
     * 启用规则
     */
    boolean enableRule(Long id);

    /**
     * 禁用规则
     */
    boolean disableRule(Long id);

    /**
     * 新增或更新计算规则
     */
    boolean saveOrUpdateRule(CalculationRule rule);

    /**
     * 删除规则
     */
    boolean removeRule(Long id);
}