package com.kh.replay.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kh.replay.global.common.ResponseData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

    // 공통 응답 메서드 
    private ResponseEntity<ResponseData<Object>> createErrorResponseEntity(Exception e, HttpStatus status) {
        return ResponseData.failure(e.getMessage(), status);
    }
    
    // 404 not bad
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseData<Object>> handleResourceNotFound(ResourceNotFoundException e) {
        log.error("데이터 없음(404): {}", e.getMessage());
        return createErrorResponseEntity(e, HttpStatus.NOT_FOUND);
    }

    //403 Forbidden
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseData<Object>> handleForbidden(ForbiddenException e) {
        log.error("권한 없음(403): {}", e.getMessage());
        return createErrorResponseEntity(e, HttpStatus.FORBIDDEN);
    }

	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ResponseData<Object>> handlerUserNotFoundException(UserNotFoundException e) {
		log.warn("사용자 찾기 실패: {}", e.getMessage());
		return ResponseData.failure(e.getMessage(), HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(CustomAuthenticationException.class)
	public ResponseEntity<ResponseData<Object>> handlerCustomAuthenticationException(CustomAuthenticationException e) {
		log.warn("사용자 찾기 실패: {}", e.getMessage());
		return ResponseData.failure(e.getMessage(), HttpStatus.NOT_FOUND);
	}

    // 400 bad request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseData<Object>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("잘못된 요청(400): {}", e.getMessage());
        return createErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    // 잘못된 상태 (IllegalStateException)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseData<Object>> handleIllegalState(IllegalStateException e) {
        log.error("잘못된 상태(400): {}", e.getMessage());
        return createErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
    }   

    // 이메일 중복 오류
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ResponseData<Object>> handlerDuplicate(DuplicateException e) {
        log.error("중복 에러(400): {}", e.getMessage());
        return createErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    // 회원가입 에러
    @ExceptionHandler(MemberJoinException.class)
    public ResponseEntity<ResponseData<Object>> handlerMemberJoinException(MemberJoinException e){
        log.error("회원가입 에러(400): {}", e.getMessage());
        return createErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
    }
    
    // 
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Object>> handleException(Exception e) {
        log.error("예상치 못한 서버 에러(500): {}", e.getMessage());
        
        return createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public ResponseEntity<ResponseData<Object>> handleOAuth2AuthenticationException(OAuth2AuthenticationException e) {
        log.error("지원하지 않는 소셜 로그인입니다:{}", e.getMessage());
        
        return createErrorResponseEntity(e, HttpStatus.BAD_REQUEST);
}
    }
