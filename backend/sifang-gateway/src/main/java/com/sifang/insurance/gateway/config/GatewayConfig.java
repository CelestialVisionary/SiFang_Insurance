package com.sifang.insurance.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API网关配置类
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 认证服务路由
                .route("sifang-auth", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://sifang-auth"))
                
                // 产品服务路由
                .route("sifang-product", r -> r
                        .path("/api/product/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://sifang-product"))
                
                // 订单服务路由
                .route("sifang-order", r -> r
                        .path("/api/order/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://sifang-order"))
                
                // 支付服务路由
                .route("sifang-payment", r -> r
                        .path("/api/payment/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://sifang-payment"))
                
                // 核保服务路由
                .route("sifang-underwriting", r -> r
                        .path("/api/underwriting/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://sifang-underwriting"))
                
                // 计算器服务路由
                .route("sifang-calculator", r -> r
                        .path("/api/calculator/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://sifang-calculator"))
                
                // 存储服务路由
                .route("sifang-storage", r -> r
                        .path("/api/storage/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://sifang-storage"))
                
                .build();
    }
}