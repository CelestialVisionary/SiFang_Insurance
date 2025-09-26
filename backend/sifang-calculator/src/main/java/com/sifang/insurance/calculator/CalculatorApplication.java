package com.sifang.insurance.calculator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 保费计算服务主应用类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.sifang.insurance.calculator.mapper")
public class CalculatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }
}