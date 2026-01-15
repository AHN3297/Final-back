package com.kh.replay.global.common;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseData<T> {
	
	private boolean success;
	private String message;
	private T data;
	
	public static <T> ResponseData<T> success(String message, T data) {
		return ResponseData.<T>builder()
						  .success(true)
						  .message(message)
						  .data(data)
						  .build();
	}
	
	public static <T> ResponseData<T> failure(String message) {
		return ResponseData.<T>builder()
						  .success(false)
						  .message(message)
						  .data(null)
						  .build();
	}
}
