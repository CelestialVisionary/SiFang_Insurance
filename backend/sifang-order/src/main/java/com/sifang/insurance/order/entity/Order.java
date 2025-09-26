package com.sifang.insurance.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@TableName("ins_order")
public class Order {
    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 保费金额
     */
    private BigDecimal premiumAmount;

    /**
     * 保险金额
     */
    private BigDecimal insuredAmount;

    /**
     * 保障期限（月）
     */
    private Integer coveragePeriod;

    /**
     * 订单状态：1-待支付，2-已支付，3-已完成，4-已取消，5-已退款
     */
    private Integer orderStatus;

    /**
     * 支付方式：1-微信，2-支付宝，3-银行卡
     */
    private Integer payMethod;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付流水号
     */
    private String payTradeNo;

    /**
     * 起保日期
     */
    private LocalDateTime effectiveDate;

    /**
     * 到期日期
     */
    private LocalDateTime expiryDate;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}