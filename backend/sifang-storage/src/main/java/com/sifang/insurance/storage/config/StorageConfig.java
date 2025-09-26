package com.sifang.insurance.storage.config;

import com.sifang.insurance.storage.service.StorageService;
import com.sifang.insurance.storage.service.impl.AliyunOSSStorageServiceImpl;
import com.sifang.insurance.storage.service.impl.LocalStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 存储服务配置类
 */
@Configuration
@EnableConfigurationProperties(OSSConfig.class)
public class StorageConfig {

    @Autowired
    private OSSConfig ossConfig;

    /**
     * 本地存储服务实现
     */
    @Bean("localStorageService")
    @ConditionalOnProperty(prefix = "oss", name = "local.enabled", havingValue = "true", matchIfMissing = true)
    public StorageService localStorageService() {
        return new LocalStorageServiceImpl();
    }

    /**
     * 阿里云OSS存储服务实现
     */
    @Bean("aliyunOSSStorageService")
    @ConditionalOnProperty(prefix = "oss", name = "local.enabled", havingValue = "false", matchIfMissing = false)
    public StorageService aliyunOSSStorageService() {
        return new AliyunOSSStorageServiceImpl();
    }

    /**
     * 主存储服务（根据配置选择具体实现）
     */
    @Bean("storageService")
    public StorageService storageService() {
        if (ossConfig.getLocal().isEnabled()) {
            return new LocalStorageServiceImpl();
        } else {
            return new AliyunOSSStorageServiceImpl();
        }
    }
}