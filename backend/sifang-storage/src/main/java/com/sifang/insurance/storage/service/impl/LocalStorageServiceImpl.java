package com.sifang.insurance.storage.service.impl;

import com.sifang.insurance.storage.config.OSSConfig;
import com.sifang.insurance.storage.service.StorageService;
import com.sifang.insurance.storage.vo.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 本地存储服务实现类
 */
@Slf4j
@Service("localStorageService")
@ConditionalOnProperty(prefix = "oss", name = "local.enabled", havingValue = "true", matchIfMissing = true)
public class LocalStorageServiceImpl implements StorageService {

    @Autowired
    private OSSConfig ossConfig;

    @Override
    public UploadResult upload(MultipartFile file, String objectName) {
        UploadResult result = new UploadResult();
        result.setOriginalFilename(file.getOriginalFilename());
        result.setStorageType("LOCAL_STORAGE");
        
        try {
            // 生成文件名称
            String fileName = generateFileName(file.getOriginalFilename(), objectName);
            String filePath = getFilePath(fileName);
            
            // 确保目录存在
            File targetFile = new File(filePath);
            if (!targetFile.getParentFile().exists()) {
                if (!targetFile.getParentFile().mkdirs()) {
                    throw new RuntimeException("创建目录失败: " + targetFile.getParentFile().getAbsolutePath());
                }
            }
            
            // 保存文件
            file.transferTo(targetFile);
            log.info("文件上传成功: {}", filePath);
            
            // 设置结果
            result.setObjectName(fileName);
            result.setSize(file.getSize());
            result.setContentType(file.getContentType());
            result.setExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
            result.setUploadTime(new Date());
            result.setUrl(buildFileUrl(fileName));
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
        result.setStorageType("LOCAL_STORAGE");
        
        try {
            // 生成文件名称
            String fileName = generateFileName(null, objectName);
            String filePath = getFilePath(fileName);
            
            // 确保目录存在
            File targetFile = new File(filePath);
            if (!targetFile.getParentFile().exists()) {
                if (!targetFile.getParentFile().mkdirs()) {
                    throw new RuntimeException("创建目录失败: " + targetFile.getParentFile().getAbsolutePath());
                }
            }
            
            // 保存文件
            try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            log.info("文件上传成功: {}", filePath);
            
            // 设置结果
            result.setObjectName(fileName);
            result.setSize(targetFile.length());
            result.setContentType(contentType);
            result.setExtension(FilenameUtils.getExtension(fileName));
            result.setUploadTime(new Date());
            result.setUrl(buildFileUrl(fileName));
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
            String filePath = getFilePath(objectName);
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException("文件不存在: " + filePath);
            }
            return new FileInputStream(file);
        } catch (Exception e) {
            log.error("下载文件失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean delete(String objectName) {
        try {
            String filePath = getFilePath(objectName);
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                log.info("删除文件成功: {}", filePath);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Boolean> batchDelete(String[] objectNames) {
        Map<String, Boolean> results = new HashMap<>(objectNames.length);
        
        for (String objectName : objectNames) {
            boolean success = delete(objectName);
            results.put(objectName, success);
        }
        
        return results;
    }

    @Override
    public String getObjectUrl(String objectName, Integer expires) {
        // 本地存储直接返回访问URL
        return buildFileUrl(objectName);
    }

    @Override
    public String generatePresignedUrl(String objectName, String contentType, Integer expires) {
        // 本地存储直接返回访问URL
        return buildFileUrl(objectName);
    }

    @Override
    public boolean exists(String objectName) {
        try {
            String filePath = getFilePath(objectName);
            return new File(filePath).exists();
        } catch (Exception e) {
            log.error("检查文件是否存在失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getStorageType() {
        return "LOCAL_STORAGE";
    }

    /**
     * 生成文件名称
     */
    private String generateFileName(String originalFilename, String objectName) {
        StringBuilder result = new StringBuilder();
        
        // 添加前缀
        if (ossConfig.getLocal().getPrefix() != null && !ossConfig.getLocal().getPrefix().isEmpty()) {
            result.append(ossConfig.getLocal().getPrefix()).append("/");
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
     * 获取文件的完整路径
     */
    private String getFilePath(String fileName) {
        return ossConfig.getLocal().getBasePath() + File.separator + fileName;
    }

    /**
     * 构建文件访问URL
     */
    private String buildFileUrl(String fileName) {
        String baseUrl = ossConfig.getLocal().getBaseUrl();
        if (baseUrl.endsWith("/")) {
            return baseUrl + fileName;
        } else {
            return baseUrl + "/" + fileName;
        }
    }
}