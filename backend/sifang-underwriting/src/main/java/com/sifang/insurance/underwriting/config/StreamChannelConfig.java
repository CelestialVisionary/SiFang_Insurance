package com.sifang.insurance.underwriting.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 消息通道配置类
 * 用于核保服务与其他服务之间的消息通信
 */
public interface StreamChannelConfig {

    // 输入通道：接收待核保的订单消息
    String UNDERWRITING_INPUT = "underwritingInput";
    
    // 输出通道：发送核保结果
    String UNDERWRITING_OUTPUT = "underwritingOutput";
    
    /**
     * 核保输入通道
     */
    @Input(UNDERWRITING_INPUT)
    SubscribableChannel underwritingInput();
    
    /**
     * 核保输出通道
     */
    @Output(UNDERWRITING_OUTPUT)
    MessageChannel underwritingOutput();
}