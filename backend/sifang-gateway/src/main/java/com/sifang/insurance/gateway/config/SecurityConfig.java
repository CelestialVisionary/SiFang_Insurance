package com.sifang.insurance.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全配置类
 */
@Slf4j
@Component
public class SecurityConfig implements GlobalFilter, Ordered {

    // 不需要认证的路径
    private static final List<String> WHITE_LIST = new ArrayList<>();

    static {
        // 认证相关
        WHITE_LIST.add("/api/auth/login");
        WHITE_LIST.add("/api/auth/register");
        WHITE_LIST.add("/api/auth/refresh");
        
        // 产品相关
        WHITE_LIST.add("/api/product/list");
        WHITE_LIST.add("/api/product/detail");
        WHITE_LIST.add("/api/product/hot");
        WHITE_LIST.add("/api/product/type");
        
        // 健康检查
        WHITE_LIST.add("/actuator/health");
        
        // 存储文件访问（本地开发环境）
        WHITE_LIST.add("/api/storage/file");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("请求路径: {}", path);

        // 检查是否在白名单中
        if (isInWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 获取Authorization头
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("未授权访问: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 这里可以添加JWT令牌验证逻辑
        // TODO: 实现JWT令牌验证
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 设置过滤器优先级
        return -100;
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isInWhiteList(String path) {
        for (String whitePath : WHITE_LIST) {
            if (path.equals(whitePath) || path.startsWith(whitePath + "/")) {
                return true;
            }
        }
        return false;
    }
}