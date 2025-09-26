package com.sifang.insurance.calculator.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 保费计算记录实体类
 */
@Data
@TableName("calculation_record")
public class CalculationRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId
    private Long id;

    /**
     * 计算流水号
     */
    private String calculateNo;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 计算参数（JSON格式）
     */
    private String calculateParams;

    /**
     * 使用的规则ID
     */
    private Long ruleId;

    /**
     * 基础保费
     */
    private BigDecimal basePremium;

    /**
     * 最终保费
     */
    private BigDecimal finalPremium;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 附加费用
     */
    private BigDecimal additionalFee;

    /**
     * 计算结果说明
     */
    private String calculateDescription;

    /**
     * 计算状态：1-成功 2-失败
     */
    private Integer status;

    /**
     * 失败原因（如果有）
     */
    private String failReason;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}