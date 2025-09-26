package com.sifang.insurance.storage.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 上传结果
 */
@Data
public class UploadResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 存储对象名称
     */
    private String objectName;

    /**
     * 文件原始名称
     */
    private String originalFilename;

    /**
     * 文件URL
     */
    private String url;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 存储类型
     */
    private String storageType;

    /**
     * 存储桶名称（OSS特有）
     */
    private String bucketName;

    /**
     * 上传状态
     */
    private boolean success;

    /**
     * 错误消息
     */
    private String errorMsg;
}