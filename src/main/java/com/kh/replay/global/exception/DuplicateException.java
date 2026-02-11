package com.kh.replay.global.exception;


public class DuplicateException extends RuntimeException {
	
	//중복
	public DuplicateException(String message) {
		super(message);
	}
}