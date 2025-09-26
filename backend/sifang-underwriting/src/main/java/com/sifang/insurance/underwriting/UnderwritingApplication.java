package com.sifang.insurance.underwriting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 核保服务应用主类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UnderwritingApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnderwritingApplication.class, args);
    }

}