package com.sifang.insurance.underwriting.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.sifang.insurance.underwriting.dto.UnderwritingRequest;
import com.sifang.insurance.underwriting.dto.UnderwritingResponse;
import com.sifang.insurance.underwriting.service.UnderwritingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 核保消息监听器
 * 用于接收待核保订单并返回核保结果
 */
@Component
public class UnderwritingMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(UnderwritingMessageListener.class);
    
    @Autowired
    private UnderwritingService underwritingService;
    
    // 使用阻塞队列存储待发送的核保结果消息
    private final BlockingQueue<Message<String>> resultQueue = new LinkedBlockingQueue<>();
    
    /**
     * 处理待核保的订单消息
     * 支持手动确认消息
     */
    @Bean
    public Consumer<Message<String>> underwritingInput() {
        return message -> {
            Channel channel = null;
            Long deliveryTag = null;
            
            try {
                // 获取消息头中的RabbitMQ通道和投递标签，用于手动确认
                if (message.getHeaders().containsKey("amqp_channel")) {
                    channel = (Channel) message.getHeaders().get("amqp_channel");
                }
                if (message.getHeaders().containsKey("amqp_deliveryTag")) {
                    deliveryTag = (Long) message.getHeaders().get("amqp_deliveryTag");
                }
                
                String payload = message.getPayload();
                logger.info("接收到待核保订单消息: {}, 投递标签: {}", payload, deliveryTag);
                
                // 解析消息内容
                JSONObject jsonObject = JSON.parseObject(payload);
                
                // 创建核保请求对象
                UnderwritingRequest request = new UnderwritingRequest();
                request.setOrderId(jsonObject.getString("orderId"));
                request.setUserId(jsonObject.getLong("userId"));
                request.setProductId(jsonObject.getLong("productId"));
                request.setApplicantInfo(jsonObject.getJSONObject("applicantInfo"));
                request.setInsuredInfo(jsonObject.getJSONObject("insuredInfo"));
                
                // 执行核保流程
                UnderwritingResponse response = underwritingService.submitUnderwriting(request);
                
                // 发送核保结果
                sendUnderwritingResult(response, payload);
                
                // 手动确认消息已成功处理
                if (channel != null && deliveryTag != null) {
                    channel.basicAck(deliveryTag, false);
                    logger.info("消息确认成功，投递标签: {}", deliveryTag);
                }
                
                logger.info("核保处理完成，订单ID: {}, 核保结果: {}", 
                        response.getOrderId(), response.getStatusDesc());
                
            } catch (Exception e) {
                logger.error("处理核保请求失败: {}", e.getMessage(), e);
                
                // 发生异常时拒绝消息并重新入队或直接丢弃
                if (channel != null && deliveryTag != null) {
                    try {
                        // 拒绝消息并重新入队（最多重试3次）
                        // 这里可以根据需要实现重试计数逻辑
                        channel.basicReject(deliveryTag, true);
                        logger.warn("消息处理失败，已拒绝并重新入队，投递标签: {}", deliveryTag);
                    } catch (IOException ioException) {
                        logger.error("拒绝消息失败: {}", ioException.getMessage(), ioException);
                    }
                }
            }
        };
    }
    
    /**
     * 发送核保结果到输出通道
     */
    private void sendUnderwritingResult(UnderwritingResponse response, String originalMessage) {
        try {
            // 创建响应消息内容
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("orderId", response.getOrderId());
            resultMap.put("underwritingStatus", response.getStatus());
            resultMap.put("underwritingStatusDesc", response.getStatusDesc());
            resultMap.put("resultReason", response.getResultReason());
            resultMap.put("needManualReview", response.getNeedManualReview());
            resultMap.put("underwritingTime", response.getUnderwritingTime());
            resultMap.put("originalMessage", originalMessage);
            
            // 转换为JSON字符串
            String resultJson = JSON.toJSONString(resultMap);
            
            // 构建消息头
            Map<String, Object> headers = new HashMap<>();
            headers.put("contentType", "application/json");
            headers.put("orderId", response.getOrderId());
            
            // 创建消息并放入队列
            Message<String> message = MessageBuilder
                    .withPayload(resultJson)
                    .copyHeaders(headers)
                    .build();
            
            resultQueue.offer(message);
            logger.info("核保结果已准备发送，订单ID: {}", response.getOrderId());
            
        } catch (Exception e) {
            logger.error("准备核保结果消息失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 核保结果输出通道
     * 用于将核保结果发送到消息队列
     */
    @Bean
    public Supplier<Message<String>> underwritingOutput() {
        return () -> {
            try {
                // 从队列中获取消息，如果队列为空则返回null（不会阻塞）
                return resultQueue.poll();
            } catch (Exception e) {
                logger.error("获取核保结果消息失败: {}", e.getMessage(), e);
                return null;
            }
        };
    }
}