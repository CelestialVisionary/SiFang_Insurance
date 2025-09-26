package com.sifang.insurance.calculator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sifang.insurance.calculator.entity.PremiumRate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 保费费率Mapper接口
 */
public interface PremiumRateMapper extends BaseMapper<PremiumRate> {

    /**
     * 根据产品ID和条件查询费率
     * @param productId 产品ID
     * @param age 年龄
     * @param gender 性别
     * @return 符合条件的费率列表
     */
    List<PremiumRate> selectByProductAndCondition(
            @Param("productId") Long productId,
            @Param("age") Integer age,
            @Param("gender") Integer gender);

    /**
     * 查询有效的产品费率
     * @param productId 产品ID
     * @return 有效的费率列表
     */
    List<PremiumRate> selectValidRatesByProductId(@Param("productId") Long productId);
}