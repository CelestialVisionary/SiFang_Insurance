package com.sifang.insurance.payment.sender;

import com.alibaba.fastjson.JSON;
import com.sifang.insurance.payment.entity.PaymentRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付消息发送器
 * 用于发送支付相关的消息到其他服务
 */
@Component
public class PaymentMessageSender {

    @Autowired
    private MessageChannel output;

    /**
     * 发送支付成功消息
     */
    public boolean sendPaymentSuccessMessage(PaymentRecord record) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", record.getOrderId());
            message.put("paymentNo", record.getPaymentNo());
            message.put("userId", record.getUserId());
            message.put("amount", record.getAmount());
            message.put("paymentTime", record.getPaymentTime());
            message.put("paymentMethod", record.getPaymentMethod());
            message.put("thirdPartyPaymentNo", record.getThirdPartyPaymentNo());
            
            Message<String> springMessage = MessageBuilder
                    .withPayload(JSON.toJSONString(message))
                    .setHeader("type", "payment_success")
                    .build();
            
            return output.send(springMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送退款成功消息
     */
    public boolean sendRefundSuccessMessage(PaymentRecord record) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", record.getOrderId());
            message.put("paymentNo", record.getPaymentNo());
            message.put("userId", record.getUserId());
            message.put("amount", record.getAmount());
            message.put("refundTime", record.getUpdateTime());
            message.put("paymentMethod", record.getPaymentMethod());
            
            Message<String> springMessage = MessageBuilder
                    .withPayload(JSON.toJSONString(message))
                    .setHeader("type", "refund_success")
                    .build();
            
            return output.send(springMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送支付失败消息
     */
    public boolean sendPaymentFailedMessage(PaymentRecord record, String reason) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("orderId", record.getOrderId());
            message.put("paymentNo", record.getPaymentNo());
            message.put("userId", record.getUserId());
            message.put("amount", record.getAmount());
            message.put("paymentMethod", record.getPaymentMethod());
            message.put("reason", reason);
            message.put("failTime", record.getUpdateTime());
            
            Message<String> springMessage = MessageBuilder
                    .withPayload(JSON.toJSONString(message))
                    .setHeader("type", "payment_failed")
                    .build();
            
            return output.send(springMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}