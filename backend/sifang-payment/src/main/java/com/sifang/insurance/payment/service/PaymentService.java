package com.sifang.insurance.payment.service;

import com.sifang.insurance.payment.dto.CreatePaymentRequest;
import com.sifang.insurance.payment.dto.PaymentResponse;
import com.sifang.insurance.payment.entity.PaymentRecord;
import java.math.BigDecimal;

/**
 * 支付服务接口
 */
public interface PaymentService {
    
    /**
     * 创建支付订单
     * @param request 创建支付请求
     * @return 支付响应
     */
    PaymentResponse createPayment(CreatePaymentRequest request);
    
    /**
     * 处理支付回调
     * @param paymentMethod 支付方式
     * @param params 回调参数
     * @return 处理结果
     */
    boolean processCallback(Integer paymentMethod, String params);
    
    /**
     * 查询支付状态
     * @param paymentNo 支付流水号
     * @return 支付记录
     */
    PaymentRecord queryPaymentStatus(String paymentNo);
    
    /**
     * 根据订单ID查询支付记录
     * @param orderId 订单ID
     * @return 支付记录
     */
    PaymentRecord queryPaymentByOrderId(String orderId);
    
    /**
     * 发起退款
     * @param paymentNo 支付流水号
     * @param refundAmount 退款金额
     * @param reason 退款原因
     * @return 是否成功
     */
    boolean refund(String paymentNo, BigDecimal refundAmount, String reason);
}