package com.kh.replay.global.exception;

// 403 Forbidden (권한 없음)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}