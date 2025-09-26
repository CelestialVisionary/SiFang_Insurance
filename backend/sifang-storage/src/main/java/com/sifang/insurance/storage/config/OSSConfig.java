package com.sifang.insurance.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OSS配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OSSConfig {

    /**
     * 阿里云OSS配置
     */
    private AliyunOss aliyun;

    /**
     * 本地存储配置
     */
    private LocalStorage local;

    /**
     * 阿里云OSS配置
     */
    @Data
    public static class AliyunOss {
        /**
         * OSS服务端点
         */
        private String endpoint;

        /**
         * 访问密钥ID
         */
        private String accessKeyId;

        /**
         * 访问密钥密钥
         */
        private String accessKeySecret;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 外网访问地址
         */
        private String domain;

        /**
         * 内网访问地址
         */
        private String internalDomain;

        /**
         * 文件目录前缀
         */
        private String prefix;

        /**
         * 上传超时时间（毫秒）
         */
        private Integer uploadTimeout;

        /**
         * 最大重试次数
         */
        private Integer maxRetry;
    }

    /**
     * 本地存储配置
     */
    @Data
    public static class LocalStorage {
        /**
         * 是否启用本地存储
         */
        private Boolean enabled;

        /**
         * 本地存储路径
         */
        private String path;

        /**
         * 本地存储访问前缀
         */
        private String prefix;
    }
}