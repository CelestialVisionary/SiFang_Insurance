package com.sifang.insurance.underwriting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.underwriting.entity.UnderwritingRuleVersion;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 核保规则版本Mapper接口
 */
@Mapper
public interface UnderwritingRuleVersionMapper extends BaseMapper<UnderwritingRuleVersion> {
    
    /**
     * 根据规则ID查询所有版本
     * @param ruleId 规则ID
     * @return 规则版本列表
     */
    List<UnderwritingRuleVersion> selectByRuleId(Long ruleId);
    
    /**
     * 查询规则的最新版本号
     * @param ruleId 规则ID
     * @return 最新版本号，如果没有版本则返回0
     */
    Integer selectLatestVersion(Long ruleId);
    
    /**
     * 将规则的所有版本标记为非当前版本
     * @param ruleId 规则ID
     */
    void updateAllVersionsToNonCurrent(Long ruleId);
    
    /**
     * 根据版本ID查询规则版本
     * @param id 版本ID
     * @return 规则版本对象
     */
    UnderwritingRuleVersion selectById(Long id);
}