package com.kh.replay.global.exception;

import org.apache.http.protocol.ResponseDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kh.replay.global.common.ResponseData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {


	/* 공통 응답 포맷 */
	private ResponseEntity<ResponseData<?>> createErrorResponseEntity(Exception e, HttpStatus status) {
		ResponseEntity<ResponseData<Object>> error = ResponseData.failure(e.getMessage(),status);
		return null;
	}
	
	// 잘못된 상태 전달시
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ResponseData<?>> handleIllegalArgumentException(IllegalStateException e) {
		log.error("잘못된 상태 : {}", e.getMessage());
		return createErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
	}	
	//이메일 중복 오류 시
	@ExceptionHandler(DuplicateException.class)
	public ResponseEntity<ResponseData<Object>> handlerDuplicate(DuplicateException e) {
		log.error("중복 에러: {}", e.getMessage());
		return ResponseData.failure(e.getMessage(), HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(MemberJoinException.class)
	public ResponseEntity<ResponseData<Object>> handlerMemberJoinException(MemberJoinException e){
		log.error("회원가입에러: {}", e.getMessage());
		return ResponseData.failure(e.getMessage(), HttpStatus.BAD_REQUEST);

	}
}
