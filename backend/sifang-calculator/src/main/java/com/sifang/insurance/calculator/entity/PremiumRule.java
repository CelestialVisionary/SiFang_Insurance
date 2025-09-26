package com.sifang.insurance.calculator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保费计算规则实体类
 */
@Data
@TableName("ins_premium_rule")
public class PremiumRule {
    /**
     * 规则ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型：1-基础费率，2-年龄系数，3-职业系数，4-地区系数
     */
    private Integer ruleType;

    /**
     * 规则条件（JSON格式）
     */
    private String conditions;

    /**
     * 规则值
     */
    private BigDecimal ruleValue;

    /**
     * 计算表达式
     */
    private String expression;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}