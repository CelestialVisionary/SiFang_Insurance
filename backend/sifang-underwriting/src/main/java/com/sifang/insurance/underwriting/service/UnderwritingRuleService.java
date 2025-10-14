package com.sifang.insurance.underwriting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sifang.insurance.underwriting.entity.UnderwritingRule;
import java.util.List;
import java.util.Map;

/**
 * 核保规则服务接口
 */
public interface UnderwritingRuleService extends IService<UnderwritingRule> {
    
    /**
     * 根据产品ID获取启用的核保规则列表
     * @param productId 产品ID
     * @return 核保规则列表
     */
    List<UnderwritingRule> getEnabledRulesByProductId(Long productId);
    
    /**
     * 保存核保规则
     * @param rule 核保规则
     * @return 是否保存成功
     */
    boolean saveRule(UnderwritingRule rule);
    
    /**
     * 保存核保规则（带版本管理）
     * @param rule 核保规则对象
     * @param versionRemark 版本说明
     * @param createBy 创建人
     * @return 是否保存成功
     */
    boolean saveRule(UnderwritingRule rule, String versionRemark, String createBy);
    
    /**
     * 验证核保规则语法
     * @param ruleContent 规则内容
     * @return 验证结果，包含是否通过和错误信息
     */
    Map<String, Object> validateRuleSyntax(String ruleContent);
    
    /**
     * 更新核保规则状态
     * @param id 规则ID
     * @param status 状态：0-禁用 1-启用
     * @return 是否更新成功
     */
    boolean updateRuleStatus(Long id, Integer status);
    
    /**
     * 删除核保规则
     * @param id 规则ID
     * @return 是否删除成功
     */
    boolean deleteRule(Long id);
}