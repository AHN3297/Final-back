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

    // 1. 공통 응답 생성 메서드
    private ResponseEntity<ResponseData<Object>> createErrorResponseEntity(String message, HttpStatus status) {
        return ResponseData.failure(message, status);
    }
    
    // *******************400 Bad Request*******************
    
    // 잘못된 요청 (파라미터 누락, 값 오류 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseData<Object>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("잘못된 요청(400): {}", e.getMessage());
        return createErrorResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 잘못된 상태 (로직 흐름 오류)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseData<Object>> handleIllegalState(IllegalStateException e) {
        log.warn("잘못된 상태(400): {}", e.getMessage());
        return createErrorResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }   

    // 이메일/닉네임 중복
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ResponseData<Object>> handlerDuplicate(DuplicateException e) {
        log.warn("중복 에러(400): {}", e.getMessage());
        return createErrorResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 회원가입 실패
    @ExceptionHandler(MemberJoinException.class)
    public ResponseEntity<ResponseData<Object>> handlerMemberJoinException(MemberJoinException e){
        log.warn("회원가입 에러(400): {}", e.getMessage());
        return createErrorResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // *******************401 Unauthorized*******************
    
    // 인증 실패 (비밀번호 틀림, 토큰 만료 등) 
    @ExceptionHandler(CustomAuthenticationException.class)
    public ResponseEntity<ResponseData<Object>> handlerCustomAuthenticationException(CustomAuthenticationException e) {
        log.warn("인증 실패(401): {}", e.getMessage());
        return createErrorResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    //*******************403 Forbidden*******************

    // 권한 없음 (접근 불가)
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseData<Object>> handleForbidden(ForbiddenException e) {
        log.warn("권한 없음(403): {}", e.getMessage());
        return createErrorResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    // *******************404 Not Found*******************

    // 리소스 없음 (게시글 없음 등)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseData<Object>> handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("데이터 없음(404): {}", e.getMessage());
        return createErrorResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    // 사용자 없음
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseData<Object>> handlerUserNotFoundException(UserNotFoundException e) {
        log.warn("사용자 찾기 실패(404): {}", e.getMessage());
        return createErrorResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }   
    
    // 노래 없음
    @ExceptionHandler(NotFoundTracksException.class)
    public ResponseEntity<ResponseData<Object>> handlerNotFoundTracksException(NotFoundTracksException e){
    	log.warn("플레이리스트 찾기 실패: {}", e.getMessage());
    	return createErrorResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    // 순서 정보 없음
    @ExceptionHandler(NotFoundOrderListException.class)
    public ResponseEntity<ResponseData<Object>> handlerNotFoundOrderListException(NotFoundOrderListException e){
    	log.warn("순서 정보 찾기 실패: {}", e.getMessage());
    	return createErrorResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    

    //*******************500 Internal Server Error*******************

    // 그 외 예상치 못한 모든 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Object>> handleException(Exception e) {
        log.error("예상치 못한 서버 에러(500): ", e); 
        return createErrorResponseEntity("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ResponseData<Object>> handleFileUpload(FileUploadException e) {
        log.error("파일 업로드 실패(500): {}", e.getMessage(), e);
        return ResponseData.failure(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}