package com.sifang.insurance.calculator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.calculator.entity.CalculationRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 计算规则Mapper接口
 */
public interface CalculationRuleMapper extends BaseMapper<CalculationRule> {

    /**
     * 根据产品ID和状态查询启用的计算规则
     */
    List<CalculationRule> selectEnabledRulesByProductId(@Param("productId") Long productId);

    /**
     * 根据规则编码查询规则
     */
    CalculationRule selectByRuleCode(@Param("ruleCode") String ruleCode);

    /**
     * 根据产品类型查询规则列表
     */
    List<CalculationRule> selectRulesByProductType(@Param("productType") Integer productType);

    /**
     * 更新规则状态
     */
    int updateRuleStatus(@Param("id") Long id, @Param("status") Integer status);
}