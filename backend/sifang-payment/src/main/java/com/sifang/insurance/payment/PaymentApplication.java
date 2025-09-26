package com.sifang.insurance.payment;

import com.sifang.insurance.payment.config.StreamChannelConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;

/**
 * 支付服务主应用类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableBinding(StreamChannelConfig.class)
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

}