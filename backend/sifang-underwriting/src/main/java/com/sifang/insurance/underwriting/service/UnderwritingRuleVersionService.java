package com.sifang.insurance.underwriting.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sifang.insurance.underwriting.entity.UnderwritingRule;
import com.sifang.insurance.underwriting.entity.UnderwritingRuleVersion;
import java.util.List;

/**
 * 核保规则版本服务接口
 */
public interface UnderwritingRuleVersionService extends IService<UnderwritingRuleVersion> {
    
    /**
     * 创建规则新版本
     * @param rule 规则对象
     * @param versionRemark 版本说明
     * @param createBy 创建人
     * @return 创建的版本对象
     */
    UnderwritingRuleVersion createVersion(UnderwritingRule rule, String versionRemark, String createBy);
    
    /**
     * 根据规则ID查询所有版本
     * @param ruleId 规则ID
     * @return 规则版本列表
     */
    List<UnderwritingRuleVersion> getVersionsByRuleId(Long ruleId);
    
    /**
     * 根据版本ID获取版本详情
     * @param versionId 版本ID
     * @return 规则版本对象
     */
    UnderwritingRuleVersion getVersionById(Long versionId);
    
    /**
     * 回滚到指定版本
     * @param versionId 版本ID
     * @return 是否回滚成功
     */
    boolean rollbackToVersion(Long versionId);
}