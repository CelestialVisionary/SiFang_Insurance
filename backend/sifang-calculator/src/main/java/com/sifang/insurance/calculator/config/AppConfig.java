package com.sifang.insurance.calculator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 应用程序配置类
 */
@Configuration
public class AppConfig {

    /**
     * 创建RestTemplate Bean
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 计算服务配置
     */
    @Configuration
    @ConfigurationProperties(prefix = "calculator")
    public static class CalculatorProperties {

        /**
         * 默认规则ID
         */
        private Long defaultRuleId = 1L;

        /**
         * 是否启用缓存
         */
        private boolean cacheEnabled = true;

        /**
         * 缓存过期时间（秒）
         */
        private int cacheExpireTime = 3600;

        /**
         * 最大计算参数数量
         */
        private int maxParamsCount = 100;

        public Long getDefaultRuleId() {
            return defaultRuleId;
        }

        public void setDefaultRuleId(Long defaultRuleId) {
            this.defaultRuleId = defaultRuleId;
        }

        public boolean isCacheEnabled() {
            return cacheEnabled;
        }

        public void setCacheEnabled(boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }

        public int getCacheExpireTime() {
            return cacheExpireTime;
        }

        public void setCacheExpireTime(int cacheExpireTime) {
            this.cacheExpireTime = cacheExpireTime;
        }

        public int getMaxParamsCount() {
            return maxParamsCount;
        }

        public void setMaxParamsCount(int maxParamsCount) {
            this.maxParamsCount = maxParamsCount;
        }
    }
}