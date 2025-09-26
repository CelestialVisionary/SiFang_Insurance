package com.sifang.insurance.calculator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 应用配置类
 */
@Configuration
public class ApplicationConfig {

    /**
     * 配置Jackson消息转换器
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册Java 8时间模块
        objectMapper.registerModule(new JavaTimeModule());
        
        // 配置属性命名策略
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}