package com.sifang.insurance.calculator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.calculator.entity.PremiumRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 保费计算规则Mapper接口
 */
@Mapper
public interface PremiumRuleMapper extends BaseMapper<PremiumRule> {
    
    /**
     * 根据产品ID和状态查询规则列表
     */
    List<PremiumRule> selectByProductIdAndStatus(@Param("productId") Long productId, @Param("status") Integer status);
    
    /**
     * 根据产品ID和规则类型查询规则列表
     */
    List<PremiumRule> selectByProductIdAndType(@Param("productId") Long productId, @Param("ruleType") Integer ruleType);
}