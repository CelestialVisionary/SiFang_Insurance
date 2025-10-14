package com.sifang.insurance.underwriting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sifang.insurance.underwriting.entity.UnderwritingRule;
import com.sifang.insurance.underwriting.mapper.UnderwritingRuleMapper;
import com.sifang.insurance.underwriting.service.UnderwritingRuleService;
import com.sifang.insurance.underwriting.service.UnderwritingRuleVersionService;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核保规则服务实现类
 */
@Service
public class UnderwritingRuleServiceImpl extends ServiceImpl<UnderwritingRuleMapper, UnderwritingRule> implements UnderwritingRuleService {

    @Autowired
    private UnderwritingRuleMapper underwritingRuleMapper;
    
    @Autowired
    private UnderwritingRuleVersionService underwritingRuleVersionService;

    @Override
    public List<UnderwritingRule> getEnabledRulesByProductId(Long productId) {
        return underwritingRuleMapper.selectEnabledRulesByProductId(productId);
    }

    @Override
    public boolean saveRule(UnderwritingRule rule) {
        // 先验证规则语法
        Map<String, Object> validateResult = validateRuleSyntax(rule.getRuleContent());
        if (!(boolean) validateResult.get("valid")) {
            throw new IllegalArgumentException("规则语法错误: " + validateResult.get("errorMessage"));
        }
        
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
    @Transactional
    public boolean saveRule(UnderwritingRule rule, String versionRemark, String createBy) {
        // 先验证规则语法
        Map<String, Object> validateResult = validateRuleSyntax(rule.getRuleContent());
        if (!(boolean) validateResult.get("valid")) {
            throw new IllegalArgumentException("规则语法错误: " + validateResult.get("errorMessage"));
        }
        
        // 设置默认值
        if (rule.getStatus() == null) {
            rule.setStatus(1); // 默认启用
        }
        if (rule.getPriority() == null) {
            rule.setPriority(100); // 默认优先级
        }
        
        boolean isUpdate = rule.getId() != null;
        boolean saveResult = this.saveOrUpdate(rule);
        
        // 如果是更新操作且保存成功，则创建新版本
        if (isUpdate && saveResult && versionRemark != null) {
            underwritingRuleVersionService.createVersion(rule, versionRemark, createBy);
        }
        
        return saveResult;
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
    
    @Override
    public Map<String, Object> validateRuleSyntax(String ruleContent) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("errorMessage", "");
        
        try {
            // 使用Drools引擎验证规则语法
            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kfs = kieServices.newKieFileSystem();
            
            // 写入规则内容进行编译验证
            kfs.write("src/main/resources/rules/validation.drl", 
                    ResourceFactory.newByteArrayResource(ruleContent.getBytes("UTF-8")));
            
            KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
            
            // 检查是否有编译错误
            if (kieBuilder.getResults().hasMessages()) {
                result.put("valid", false);
                result.put("errorMessage", kieBuilder.getResults().toString());
            }
        } catch (Exception e) {
            result.put("valid", false);
            result.put("errorMessage", "规则验证失败: " + e.getMessage());
        }
        
        return result;
    }
}