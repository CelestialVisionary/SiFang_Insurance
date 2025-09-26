package com.sifang.insurance.storage.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.sifang.insurance.storage.config.OSSConfig;
import com.sifang.insurance.storage.service.StorageService;
import com.sifang.insurance.storage.vo.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 阿里云OSS存储服务实现类
 */
@Slf4j
@Service("aliyunOSSStorageService")
@ConditionalOnProperty(prefix = "oss", name = "local.enabled", havingValue = "false", matchIfMissing = false)
public class AliyunOSSStorageServiceImpl implements StorageService {

    @Autowired
    private OSSConfig ossConfig;

    private OSS ossClient;

    /**
     * 获取OSS客户端
     */
    private OSS getOssClient() {
        if (ossClient == null) {
            synchronized (this) {
                if (ossClient == null) {
                    ossClient = new OSSClientBuilder()
                            .build(
                                    ossConfig.getAliyun().getEndpoint(),
                                    ossConfig.getAliyun().getAccessKeyId(),
                                    ossConfig.getAliyun().getAccessKeySecret()
                            );
                }
            }
        }
        return ossClient;
    }

    /**
     * 关闭OSS客户端
     */
    private void closeOssClient() {
        if (ossClient != null) {
            ossClient.shutdown();
            ossClient = null;
        }
    }

    @Override
    public UploadResult upload(MultipartFile file, String objectName) {
        UploadResult result = new UploadResult();
        result.setOriginalFilename(file.getOriginalFilename());
        result.setStorageType("ALIYUN_OSS");

        try {
            // 生成对象名称
            String finalObjectName = generateObjectName(file.getOriginalFilename(), objectName);
            result.setObjectName(finalObjectName);
            result.setSize(file.getSize());
            result.setContentType(file.getContentType());
            result.setExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
            result.setUploadTime(new Date());
            result.setBucketName(ossConfig.getAliyun().getBucketName());

            // 设置上传请求参数
            PutObjectRequest request = new PutObjectRequest(
                    ossConfig.getAliyun().getBucketName(),
                    finalObjectName,
                    file.getInputStream()
            );

            // 设置文件元信息
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            request.setMetadata(metadata);

            // 执行上传
            PutObjectResult putObjectResult = getOssClient().putObject(request);
            log.info("文件上传成功: {}, ETag: {}", finalObjectName, putObjectResult.getETag());

            // 构建访问URL
            result.setUrl(buildObjectUrl(finalObjectName));
            result.setSuccess(true);

        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }

        return result;
    }

    @Override
    public UploadResult upload(InputStream inputStream, String objectName, String contentType) {
        UploadResult result = new UploadResult();
        result.setStorageType("ALIYUN_OSS");

        try {
            // 生成对象名称
            String finalObjectName = generateObjectName(null, objectName);
            result.setObjectName(finalObjectName);
            result.setContentType(contentType);
            result.setExtension(FilenameUtils.getExtension(objectName));
            result.setUploadTime(new Date());
            result.setBucketName(ossConfig.getAliyun().getBucketName());

            // 设置上传请求参数
            PutObjectRequest request = new PutObjectRequest(
                    ossConfig.getAliyun().getBucketName(),
                    finalObjectName,
                    inputStream
            );

            // 设置文件元信息
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            request.setMetadata(metadata);

            // 执行上传
            PutObjectResult putObjectResult = getOssClient().putObject(request);
            log.info("文件上传成功: {}, ETag: {}", finalObjectName, putObjectResult.getETag());

            // 获取文件大小
            ObjectMetadata objectMetadata = getOssClient().getObjectMetadata(
                    ossConfig.getAliyun().getBucketName(),
                    finalObjectName
            );
            result.setSize(objectMetadata.getContentLength());

            // 构建访问URL
            result.setUrl(buildObjectUrl(finalObjectName));
            result.setSuccess(true);

        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error("关闭输入流失败: {}", e.getMessage(), e);
            }
        }

        return result;
    }

    @Override
    public Map<String, UploadResult> batchUpload(MultipartFile[] files, String prefix) {
        Map<String, UploadResult> results = new HashMap<>(files.length);

        for (MultipartFile file : files) {
            try {
                String objectName = prefix != null ? prefix + "/" + file.getOriginalFilename() : null;
                UploadResult result = upload(file, objectName);
                results.put(file.getOriginalFilename(), result);
            } catch (Exception e) {
                log.error("批量上传文件失败: {}", e.getMessage(), e);
                UploadResult errorResult = new UploadResult();
                errorResult.setOriginalFilename(file.getOriginalFilename());
                errorResult.setSuccess(false);
                errorResult.setErrorMsg(e.getMessage());
                results.put(file.getOriginalFilename(), errorResult);
            }
        }

        return results;
    }

    @Override
    public InputStream download(String objectName) {
        try {
            OSSObject ossObject = getOssClient().getObject(
                    ossConfig.getAliyun().getBucketName(),
                    objectName
            );
            return ossObject.getObjectContent();
        } catch (Exception e) {
            log.error("下载文件失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean delete(String objectName) {
        try {
            getOssClient().deleteObject(
                    ossConfig.getAliyun().getBucketName(),
                    objectName
            );
            log.info("删除文件成功: {}", objectName);
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Boolean> batchDelete(String[] objectNames) {
        Map<String, Boolean> results = new HashMap<>(objectNames.length);

        try {
            List<String> keys = Arrays.asList(objectNames);
            DeleteObjectsResult deleteObjectsResult = getOssClient().deleteObjects(
                    new DeleteObjectsRequest(ossConfig.getAliyun().getBucketName())
                            .withKeys(keys)
                            .withEncodingType("url")
            );

            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
            for (String objectName : objectNames) {
                results.put(objectName, deletedObjects.contains(objectName));
            }

        } catch (Exception e) {
            log.error("批量删除文件失败: {}", e.getMessage(), e);
            for (String objectName : objectNames) {
                results.put(objectName, false);
            }
        }

        return results;
    }

    @Override
    public String getObjectUrl(String objectName, Integer expires) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expires));
            URL url = getOssClient().generatePresignedUrl(
                    ossConfig.getAliyun().getBucketName(),
                    objectName,
                    expiration
            );
            return url.toString();
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String generatePresignedUrl(String objectName, String contentType, Integer expires) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expires));
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    ossConfig.getAliyun().getBucketName(),
                    objectName,
                    HttpMethod.PUT
            );

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            request.setMetadata(metadata);
            request.setExpiration(expiration);

            URL url = getOssClient().generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            log.error("生成预签名上传URL失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean exists(String objectName) {
        try {
            getOssClient().getObjectMetadata(
                    ossConfig.getAliyun().getBucketName(),
                    objectName
            );
            return true;
        } catch (OSSException e) {
            if ("NoSuchKey".equals(e.getErrorCode())) {
                return false;
            }
            throw e;
        } catch (Exception e) {
            log.error("检查文件是否存在失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getStorageType() {
        return "ALIYUN_OSS";
    }

    /**
     * 生成对象名称
     */
    private String generateObjectName(String originalFilename, String objectName) {
        StringBuilder result = new StringBuilder();
        
        // 添加前缀
        if (ossConfig.getAliyun().getPrefix() != null && !ossConfig.getAliyun().getPrefix().isEmpty()) {
            result.append(ossConfig.getAliyun().getPrefix()).append("/");
        }
        
        // 添加日期目录
        result.append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append("/");
        
        // 添加文件名
        if (objectName != null) {
            result.append(objectName);
        } else {
            // 生成随机文件名
            result.append(UUID.randomUUID().toString().replace("-", ""));
            if (originalFilename != null) {
                String extension = FilenameUtils.getExtension(originalFilename);
                if (!extension.isEmpty()) {
                    result.append(".").append(extension);
                }
            }
        }
        
        return result.toString();
    }

    /**
     * 构建对象访问URL
     */
    private String buildObjectUrl(String objectName) {
        String domain = ossConfig.getAliyun().getDomain();
        if (domain.endsWith("/")) {
            return domain + objectName;
        } else {
            return domain + "/" + objectName;
        }
    }
}