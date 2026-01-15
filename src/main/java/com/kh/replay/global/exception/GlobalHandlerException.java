package com.kh.replay.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kh.replay.global.common.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

	/* 공통 응답 포맷 */
	private ResponseEntity<ApiResponse<?>> createErrorResponseEntity(Exception e, HttpStatus status) {
		ApiResponse<?> error = ApiResponse.failure(e.getMessage());
		return ResponseEntity.status(status).body(error);
	}
	
	// 잘못된 상태 전달시
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalStateException e) {
		log.error("잘못된 상태 : {}", e.getMessage());
		return createErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
	}

	
}
