package com.sifang.insurance.underwriting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sifang.insurance.underwriting.entity.UnderwritingRule;
import com.sifang.insurance.underwriting.mapper.UnderwritingRuleMapper;
import com.sifang.insurance.underwriting.service.UnderwritingRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 核保规则服务实现类
 */
@Service
public class UnderwritingRuleServiceImpl extends ServiceImpl<UnderwritingRuleMapper, UnderwritingRule> implements UnderwritingRuleService {

    @Autowired
    private UnderwritingRuleMapper underwritingRuleMapper;

    @Override
    public List<UnderwritingRule> getEnabledRulesByProductId(Long productId) {
        return underwritingRuleMapper.selectEnabledRulesByProductId(productId);
    }

    @Override
    public boolean saveRule(UnderwritingRule rule) {
        // 设置默认值
        if (rule.getStatus() == null) {
            rule.setStatus(1); // 默认启用
        }
        if (rule.getPriority() == null) {
            rule.setPriority(100); // 默认优先级
        }
        return this.save(rule);
    }

    @Override
    public boolean updateRuleStatus(Long id, Integer status) {
        UnderwritingRule rule = new UnderwritingRule();
        rule.setId(id);
        rule.setStatus(status);
        return this.updateById(rule);
    }

    @Override
    public boolean deleteRule(Long id) {
        return this.removeById(id);
    }
}