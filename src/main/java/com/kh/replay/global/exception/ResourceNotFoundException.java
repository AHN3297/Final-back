package com.kh.replay.global.exception;

// 404 Not Found (데이터 없음)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}