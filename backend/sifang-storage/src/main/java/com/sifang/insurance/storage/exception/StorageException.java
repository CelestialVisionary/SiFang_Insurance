package com.sifang.insurance.storage.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 存储服务异常类
 */
@Getter
public class StorageException extends RuntimeException {

    private HttpStatus status;

    public StorageException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public StorageException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public StorageException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}