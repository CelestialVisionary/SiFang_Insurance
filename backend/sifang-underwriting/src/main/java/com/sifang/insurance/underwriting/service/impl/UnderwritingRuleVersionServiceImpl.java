package com.sifang.insurance.underwriting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sifang.insurance.underwriting.entity.UnderwritingRule;
import com.sifang.insurance.underwriting.entity.UnderwritingRuleVersion;
import com.sifang.insurance.underwriting.mapper.UnderwritingRuleMapper;
import com.sifang.insurance.underwriting.mapper.UnderwritingRuleVersionMapper;
import com.sifang.insurance.underwriting.service.UnderwritingRuleVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 核保规则版本服务实现类
 */
@Service
public class UnderwritingRuleVersionServiceImpl extends ServiceImpl<UnderwritingRuleVersionMapper, UnderwritingRuleVersion> implements UnderwritingRuleVersionService {

    @Autowired
    private UnderwritingRuleVersionMapper underwritingRuleVersionMapper;
    
    @Autowired
    private UnderwritingRuleMapper underwritingRuleMapper;

    @Override
    @Transactional
    public UnderwritingRuleVersion createVersion(UnderwritingRule rule, String versionRemark, String createBy) {
        // 获取当前规则的最新版本号
        Integer latestVersion = underwritingRuleVersionMapper.selectLatestVersion(rule.getId());
        Integer newVersion = latestVersion + 1;
        
        // 将所有现有版本标记为非当前版本
        underwritingRuleVersionMapper.updateAllVersionsToNonCurrent(rule.getId());
        
        // 创建新版本
        UnderwritingRuleVersion version = new UnderwritingRuleVersion();
        version.setRuleId(rule.getId());
        version.setVersion(newVersion);
        version.setRuleContent(rule.getRuleContent());
        version.setRuleName(rule.getRuleName());
        version.setRuleDescription(rule.getRuleDescription());
        version.setRuleType(rule.getRuleType());
        version.setProductId(rule.getProductId());
        version.setPriority(rule.getPriority());
        version.setVersionRemark(versionRemark);
        version.setCreateBy(createBy);
        version.setCreateTime(new Date());
        version.setIsCurrent(true);
        
        underwritingRuleVersionMapper.insert(version);
        return version;
    }

    @Override
    public List<UnderwritingRuleVersion> getVersionsByRuleId(Long ruleId) {
        return underwritingRuleVersionMapper.selectByRuleId(ruleId);
    }

    @Override
    public UnderwritingRuleVersion getVersionById(Long versionId) {
        return underwritingRuleVersionMapper.selectById(versionId);
    }

    @Override
    @Transactional
    public boolean rollbackToVersion(Long versionId) {
        // 获取要回滚到的版本
        UnderwritingRuleVersion targetVersion = underwritingRuleVersionMapper.selectById(versionId);
        if (targetVersion == null) {
            return false;
        }
        
        // 获取当前规则
        UnderwritingRule currentRule = underwritingRuleMapper.selectById(targetVersion.getRuleId());
        if (currentRule == null) {
            return false;
        }
        
        // 更新规则内容为指定版本的内容
        currentRule.setRuleContent(targetVersion.getRuleContent());
        currentRule.setRuleName(targetVersion.getRuleName());
        currentRule.setRuleDescription(targetVersion.getRuleDescription());
        currentRule.setRuleType(targetVersion.getRuleType());
        currentRule.setProductId(targetVersion.getProductId());
        currentRule.setPriority(targetVersion.getPriority());
        currentRule.setUpdateTime(new Date());
        
        underwritingRuleMapper.updateById(currentRule);
        
        // 将所有版本标记为非当前版本
        underwritingRuleVersionMapper.updateAllVersionsToNonCurrent(targetVersion.getRuleId());
        
        // 创建新版本记录，作为回滚后的当前版本
        UnderwritingRuleVersion newCurrentVersion = new UnderwritingRuleVersion();
        newCurrentVersion.setRuleId(targetVersion.getRuleId());
        newCurrentVersion.setVersion(underwritingRuleVersionMapper.selectLatestVersion(targetVersion.getRuleId()) + 1);
        newCurrentVersion.setRuleContent(targetVersion.getRuleContent());
        newCurrentVersion.setRuleName(targetVersion.getRuleName());
        newCurrentVersion.setRuleDescription(targetVersion.getRuleDescription());
        newCurrentVersion.setRuleType(targetVersion.getRuleType());
        newCurrentVersion.setProductId(targetVersion.getProductId());
        newCurrentVersion.setPriority(targetVersion.getPriority());
        newCurrentVersion.setVersionRemark("回滚到版本 " + targetVersion.getVersion());
        newCurrentVersion.setCreateBy("system");
        newCurrentVersion.setCreateTime(new Date());
        newCurrentVersion.setIsCurrent(true);
        
        underwritingRuleVersionMapper.insert(newCurrentVersion);
        
        return true;
    }
}