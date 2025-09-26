package com.sifang.insurance.payment.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Spring Cloud Stream消息通道配置
 */
public interface StreamChannelConfig {

    /**
     * 订单创建消息通道
     */
    String ORDER_CREATED_CHANNEL = "orderCreatedChannel";
    
    /**
     * 订单取消消息通道
     */
    String ORDER_CANCELED_CHANNEL = "orderCanceledChannel";
    
    /**
     * 支付成功消息通道
     */
    String PAYMENT_SUCCESS_CHANNEL = "paymentSuccessChannel";
    
    /**
     * 退款成功消息通道
     */
    String REFUND_SUCCESS_CHANNEL = "refundSuccessChannel";
    
    /**
     * 通用输出通道
     */
    String OUTPUT_CHANNEL = "output";
    
    /**
     * 获取订单创建消息输入通道
     */
    @Input(ORDER_CREATED_CHANNEL)
    SubscribableChannel orderCreatedChannel();
    
    /**
     * 获取订单取消消息输入通道
     */
    @Input(ORDER_CANCELED_CHANNEL)
    SubscribableChannel orderCanceledChannel();
    
    /**
     * 获取通用输出通道
     */
    @Output(OUTPUT_CHANNEL)
    MessageChannel output();
}