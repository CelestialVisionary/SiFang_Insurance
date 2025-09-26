package com.sifang.insurance.calculator.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sifang.insurance.calculator.entity.PremiumRule;
import java.util.List;

/**
 * 保费计算规则服务接口
 */
public interface PremiumRuleService extends IService<PremiumRule> {
    
    /**
     * 根据产品ID获取启用的规则列表
     */
    List<PremiumRule> getActiveRulesByProductId(Long productId);
    
    /**
     * 根据产品ID和规则类型获取规则列表
     */
    List<PremiumRule> getRulesByProductIdAndType(Long productId, Integer ruleType);
    
    /**
     * 新增规则
     */
    boolean createRule(PremiumRule rule);
    
    /**
     * 更新规则
     */
    boolean updateRule(PremiumRule rule);
    
    /**
     * 更新规则状态
     */
    boolean updateRuleStatus(Long id, Integer status);
    
    /**
     * 删除规则
     */
    boolean deleteRule(Long id);
}