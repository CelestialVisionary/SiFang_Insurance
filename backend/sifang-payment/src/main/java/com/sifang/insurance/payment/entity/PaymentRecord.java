package com.sifang.insurance.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付记录实体类
 */
@Data
@TableName("payment_record")
public class PaymentRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    // 支付流水号
    private String paymentNo;
    
    // 订单ID
    private String orderId;
    
    // 用户ID
    private Long userId;
    
    // 支付金额
    private BigDecimal amount;
    
    // 支付方式：1-支付宝 2-微信支付 3-银联支付
    private Integer paymentMethod;
    
    // 支付状态：0-待支付 1-支付成功 2-支付失败 3-退款中 4-已退款
    private Integer status;
    
    // 支付时间
    private Date paymentTime;
    
    // 第三方支付流水号
    private String thirdPartyPaymentNo;
    
    // 支付备注
    private String remark;
    
    // 创建时间
    private Date createTime;
    
    // 更新时间
    private Date updateTime;
}