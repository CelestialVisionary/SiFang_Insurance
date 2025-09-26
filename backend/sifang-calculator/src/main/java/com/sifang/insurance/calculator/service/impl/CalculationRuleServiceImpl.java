package com.sifang.insurance.calculator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sifang.insurance.calculator.entity.CalculationRule;
import com.sifang.insurance.calculator.mapper.CalculationRuleMapper;
import com.sifang.insurance.calculator.service.CalculationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 计算规则服务实现类
 */
@Service
public class CalculationRuleServiceImpl extends ServiceImpl<CalculationRuleMapper, CalculationRule> implements CalculationRuleService {

    @Autowired
    private CalculationRuleMapper calculationRuleMapper;

    @Override
    public List<CalculationRule> getEnabledRulesByProductId(Long productId) {
        return calculationRuleMapper.selectEnabledRulesByProductId(productId);
    }

    @Override
    public CalculationRule getByRuleCode(String ruleCode) {
        return calculationRuleMapper.selectByRuleCode(ruleCode);
    }

    @Override
    public List<CalculationRule> getRulesByProductType(Integer productType) {
        return calculationRuleMapper.selectRulesByProductType(productType);
    }

    @Override
    public boolean enableRule(Long id) {
        return calculationRuleMapper.updateRuleStatus(id, 1) > 0;
    }

    @Override
    public boolean disableRule(Long id) {
        return calculationRuleMapper.updateRuleStatus(id, 0) > 0;
    }

    @Override
    public boolean saveOrUpdateRule(CalculationRule rule) {
        Date now = new Date();
        if (rule.getId() == null) {
            // 新增规则
            rule.setCreateTime(now);
            rule.setUpdateTime(now);
        } else {
            // 更新规则
            rule.setUpdateTime(now);
        }
        return this.saveOrUpdate(rule);
    }

    @Override
    public boolean removeRule(Long id) {
        return this.removeById(id);
    }
}