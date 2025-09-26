package com.sifang.insurance.payment.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sifang.insurance.payment.config.StreamChannelConfig;
import com.sifang.insurance.payment.dto.CreatePaymentRequest;
import com.sifang.insurance.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 订单消息消费者
 * 用于接收订单服务发送的消息
 */
@Component
public class OrderMessageConsumer {

    @Autowired
    private PaymentService paymentService;

    /**
     * 接收订单创建消息
     * 当订单创建成功后，自动创建支付记录
     */
    @StreamListener(StreamChannelConfig.ORDER_CREATED_CHANNEL)
    public void handleOrderCreated(@Payload String message) {
        try {
            // 解析消息
            JSONObject messageObj = JSON.parseObject(message);
            
            // 构建支付请求
            CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
            paymentRequest.setOrderId(messageObj.getString("orderId"));
            paymentRequest.setUserId(messageObj.getLong("userId"));
            paymentRequest.setAmount(messageObj.getBigDecimal("amount"));
            paymentRequest.setPaymentMethod(1); // 默认使用支付宝
            paymentRequest.setProductName(messageObj.getString("productName"));
            
            // 创建支付记录
            paymentService.createPayment(paymentRequest);
            
            // 记录日志
            System.out.println("成功为订单创建支付记录: " + messageObj.getString("orderId"));
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("处理订单创建消息失败: " + e.getMessage());
            e.printStackTrace();
            // 实际项目中可以考虑将失败消息发送到重试队列
        }
    }

    /**
     * 接收订单取消消息
     * 当订单取消时，关闭或删除相关的支付记录
     */
    @StreamListener(StreamChannelConfig.ORDER_CANCELED_CHANNEL)
    public void handleOrderCanceled(@Payload String message) {
        try {
            // 解析消息
            JSONObject messageObj = JSON.parseObject(message);
            String orderId = messageObj.getString("orderId");
            
            // 查询并处理相关支付记录
            com.sifang.insurance.payment.entity.PaymentRecord record = 
                paymentService.queryPaymentByOrderId(orderId);
            
            if (record != null && record.getStatus() == 0) { // 待支付状态
                // 在实际项目中，这里可能需要调用第三方支付平台的关闭订单接口
                // 这里简化处理，只记录日志
                System.out.println("关闭订单相关的支付记录: " + record.getPaymentNo());
            }
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("处理订单取消消息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}