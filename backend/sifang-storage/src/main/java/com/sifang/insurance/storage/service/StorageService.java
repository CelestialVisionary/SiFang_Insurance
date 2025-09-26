package com.sifang.insurance.storage.service;

import com.sifang.insurance.storage.vo.UploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

/**
 * 对象存储服务接口
 */
public interface StorageService {

    /**
     * 上传文件
     * @param file 上传的文件
     * @param objectName 存储对象名称（可选）
     * @return 上传结果
     */
    UploadResult upload(MultipartFile file, String objectName);

    /**
     * 上传文件
     * @param inputStream 文件输入流
     * @param objectName 存储对象名称
     * @param contentType 文件内容类型
     * @return 上传结果
     */
    UploadResult upload(InputStream inputStream, String objectName, String contentType);

    /**
     * 批量上传文件
     * @param files 上传的文件数组
     * @param prefix 对象名称前缀（可选）
     * @return 上传结果映射
     */
    Map<String, UploadResult> batchUpload(MultipartFile[] files, String prefix);

    /**
     * 下载文件
     * @param objectName 存储对象名称
     * @return 文件输入流
     */
    InputStream download(String objectName);

    /**
     * 删除文件
     * @param objectName 存储对象名称
     * @return 是否删除成功
     */
    boolean delete(String objectName);

    /**
     * 批量删除文件
     * @param objectNames 存储对象名称数组
     * @return 删除结果映射
     */
    Map<String, Boolean> batchDelete(String[] objectNames);

    /**
     * 获取文件访问URL
     * @param objectName 存储对象名称
     * @param expires 过期时间（秒）
     * @return 文件访问URL
     */
    String getObjectUrl(String objectName, Integer expires);

    /**
     * 生成预签名URL（用于前端直接上传）
     * @param objectName 存储对象名称
     * @param contentType 文件内容类型
     * @param expires 过期时间（秒）
     * @return 预签名URL
     */
    String generatePresignedUrl(String objectName, String contentType, Integer expires);

    /**
     * 检查文件是否存在
     * @param objectName 存储对象名称
     * @return 是否存在
     */
    boolean exists(String objectName);

    /**
     * 获取存储类型
     * @return 存储类型
     */
    String getStorageType();
}