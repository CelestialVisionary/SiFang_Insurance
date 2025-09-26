package com.sifang.insurance.payment.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 创建支付请求DTO
 */
@Data
public class CreatePaymentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    // 订单ID
    @NotNull(message = "订单ID不能为空")
    private String orderId;
    
    // 用户ID
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    // 支付金额
    @NotNull(message = "支付金额不能为空")
    private BigDecimal amount;
    
    // 支付方式：1-支付宝 2-微信支付 3-银联支付
    @NotNull(message = "支付方式不能为空")
    private Integer paymentMethod;
    
    // 商品名称
    @NotNull(message = "商品名称不能为空")
    private String productName;
    
    // 回调地址
    private String returnUrl;
    
    // 通知地址
    private String notifyUrl;
    
    // 其他参数
    private Map<String, Object> extraParams;
}