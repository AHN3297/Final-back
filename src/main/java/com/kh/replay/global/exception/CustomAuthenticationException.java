package com.kh.replay.global.exception;


//401
public class CustomAuthenticationException extends RuntimeException{
	public CustomAuthenticationException(String message) {
		super(message);
	}

}
