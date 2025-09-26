package com.sifang.insurance.storage.controller;

import com.sifang.insurance.storage.service.StorageService;
import com.sifang.insurance.storage.vo.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 存储服务控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/storage")
public class StorageController {

    @Autowired
    @Qualifier("storageService")
    private StorageService storageService;

    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    public ResponseEntity<UploadResult> upload(@RequestParam("file") MultipartFile file, 
                                             @RequestParam(value = "objectName", required = false) String objectName) {
        if (file.isEmpty()) {
            UploadResult result = new UploadResult();
            result.setSuccess(false);
            result.setErrorMsg("文件不能为空");
            return ResponseEntity.badRequest().body(result);
        }

        UploadResult result = storageService.upload(file, objectName);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 批量上传文件
     */
    @PostMapping("/upload/batch")
    public ResponseEntity<Map<String, UploadResult>> batchUpload(MultipartHttpServletRequest request,
                                                              @RequestParam(value = "prefix", required = false) String prefix) {
        Map<String, MultipartFile> fileMap = request.getFileMap();
        if (fileMap.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of());
        }

        MultipartFile[] files = fileMap.values().toArray(new MultipartFile[0]);
        Map<String, UploadResult> results = storageService.batchUpload(files, prefix);
        return ResponseEntity.ok(results);
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{objectName}")
    public ResponseEntity<byte[]> download(@PathVariable("objectName") String objectName,
                                         @RequestParam(value = "fileName", required = false) String fileName) throws IOException {
        InputStream inputStream = storageService.download(objectName);
        if (inputStream == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] bytes = inputStream.readAllBytes();
        inputStream.close();

        HttpHeaders headers = new HttpHeaders();
        if (fileName != null) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        }

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /**
     * 删除单个文件
     */
    @DeleteMapping("/delete/{objectName}")
    public ResponseEntity<Map<String, Boolean>> delete(@PathVariable("objectName") String objectName) {
        boolean success = storageService.delete(objectName);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 批量删除文件
     */
    @DeleteMapping("/delete/batch")
    public ResponseEntity<Map<String, Boolean>> batchDelete(@RequestBody String[] objectNames) {
        Map<String, Boolean> results = storageService.batchDelete(objectNames);
        return ResponseEntity.ok(results);
    }

    /**
     * 获取文件访问URL
     */
    @GetMapping("/url/{objectName}")
    public ResponseEntity<Map<String, String>> getObjectUrl(@PathVariable("objectName") String objectName,
                                                         @RequestParam(value = "expires", defaultValue = "3600") Integer expires) {
        String url = storageService.getObjectUrl(objectName, expires);
        if (url == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("url", url));
    }

    /**
     * 生成预签名上传URL
     */
    @PostMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> generatePresignedUrl(
            @RequestParam("objectName") String objectName,
            @RequestParam("contentType") String contentType,
            @RequestParam(value = "expires", defaultValue = "3600") Integer expires) {
        String url = storageService.generatePresignedUrl(objectName, contentType, expires);
        if (url == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of());
        }
        return ResponseEntity.ok(Map.of("url", url));
    }

    /**
     * 检查文件是否存在
     */
    @GetMapping("/exists/{objectName}")
    public ResponseEntity<Map<String, Boolean>> exists(@PathVariable("objectName") String objectName) {
        boolean exists = storageService.exists(objectName);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * 获取存储类型
     */
    @GetMapping("/type")
    public ResponseEntity<Map<String, String>> getStorageType() {
        return ResponseEntity.ok(Map.of("storageType", storageService.getStorageType()));
    }

    /**
     * 直接访问文件（用于本地开发环境）
     */
    @GetMapping("/file/{path:.*}")
    public void serveFile(@PathVariable("path") String path, HttpServletResponse response) throws IOException {
        InputStream inputStream = storageService.download(path);
        if (inputStream == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 设置响应头
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/octet-stream");

        // 输出文件内容
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            response.getOutputStream().write(buffer, 0, bytesRead);
        }

        inputStream.close();
        response.getOutputStream().flush();
    }
}