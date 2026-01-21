package com.kh.replay.global.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseData<T> {
   private String message;
   private Object data;
   private int status;
   private String error;
   

   // 성공 응답d
   public static <T> ResponseEntity<ResponseData<T>> ok(T data) {
      return ResponseEntity.ok(
         new ResponseData<T>(null, data, HttpStatus.OK.value(), null)
      );
   }

   public static <T> ResponseEntity<ResponseData<T>> ok(T data, String message) {
      return ResponseEntity.ok(

         new ResponseData<T>(message, data, HttpStatus.OK.value(), null)
      );
   }

   // 실패응답
   public static <T> ResponseEntity<ResponseData<T>> failure(String message, HttpStatus status) {
      return ResponseEntity.status(status)
         .body(new ResponseData<T>(message, null, status.value(), "요청실패"));
   }

   // 3. 데이터 없이 상태 코드만 보내는 경우 (204 No Content)
   public static <T> ResponseEntity<ResponseData<T>> noContent() {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
   }

   // 4. 생성 성공 (201 Created)
   public static <T> ResponseEntity<ResponseData<T>> created(T data ,String message) {
      return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseData<T>("생성되었습니다.", data, HttpStatus.CREATED.value(), null));

   }
}