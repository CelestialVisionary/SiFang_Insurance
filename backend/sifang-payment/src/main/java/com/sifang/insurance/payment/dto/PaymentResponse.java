package com.sifang.insurance.payment.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付响应DTO
 */
@Data
public class PaymentResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    // 支付流水号
    private String paymentNo;
    
    // 订单ID
    private String orderId;
    
    // 支付金额
    private BigDecimal amount;
    
    // 支付状态：0-待支付 1-支付成功 2-支付失败 3-退款中 4-已退款
    private Integer status;
    
    // 支付状态描述
    private String statusDesc;
    
    // 支付方式名称
    private String paymentMethodName;
    
    // 支付链接/二维码数据（用于前端跳转支付）
    private String paymentUrl;
    
    // 第三方支付参数
    private Map<String, String> thirdPartyParams;
    
    // 二维码过期时间（毫秒）
    private Long expireTime;
    
    // 备注信息
    private String remark;
}