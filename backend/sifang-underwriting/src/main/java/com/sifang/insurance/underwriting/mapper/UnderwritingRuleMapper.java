package com.sifang.insurance.underwriting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.underwriting.entity.UnderwritingRule;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 核保规则Mapper接口
 */
@Mapper
public interface UnderwritingRuleMapper extends BaseMapper<UnderwritingRule> {
    
    /**
     * 根据产品ID查询启用的核保规则
     * @param productId 产品ID
     * @return 核保规则列表
     */
    List<UnderwritingRule> selectEnabledRulesByProductId(Long productId);
}