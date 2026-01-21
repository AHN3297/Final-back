package com.kh.replay.global.exception;

public class MemberJoinException extends RuntimeException{
	
	//회원가입 실패 예외처리 
	public MemberJoinException(String message) {
		super(message);
	}
}
