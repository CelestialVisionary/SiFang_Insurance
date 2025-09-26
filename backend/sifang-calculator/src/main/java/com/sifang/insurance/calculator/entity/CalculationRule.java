package com.sifang.insurance.calculator.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 保费计算规则实体类
 */
@Data
@TableName("calculation_rule")
public class CalculationRule implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 规则ID
     */
    @TableId
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品类型
     */
    private Integer productType;

    /**
     * 计算方式：1-固定金额 2-比例计算 3-阶梯计算 4-规则引擎
     */
    private Integer calculationMethod;

    /**
     * 基础费率
     */
    private Double baseRate;

    /**
     * 最低保费
     */
    private Double minPremium;

    /**
     * 最高保费
     */
    private Double maxPremium;

    /**
     * 规则内容（JSON格式，存储详细的计算参数）
     */
    private String ruleContent;

    /**
     * 规则状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 备注
     */
    private String remark;
}