package com.sifang.insurance.storage.exception;

import com.sifang.insurance.storage.vo.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理类
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理文件上传异常
     */
    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    public ResponseEntity<UploadResult> handleMultipartException(MultipartException ex) {
        log.error("文件上传异常: {}", ex.getMessage(), ex);
        UploadResult result = new UploadResult();
        result.setSuccess(false);
        result.setErrorMsg("文件上传失败: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 处理文件大小超出限制异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<UploadResult> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("文件大小超出限制: {}", ex.getMessage(), ex);
        UploadResult result = new UploadResult();
        result.setSuccess(false);
        result.setErrorMsg("文件大小超出限制");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(result);
    }

    /**
     * 处理存储异常
     */
    @ExceptionHandler(StorageException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleStorageException(StorageException ex) {
        log.error("存储异常: {}", ex.getMessage(), ex);
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorMsg", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        log.error("未知异常: {}", ex.getMessage(), ex);
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorMsg", "服务器内部错误: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}