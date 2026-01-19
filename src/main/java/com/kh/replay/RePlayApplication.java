package com.kh.replay;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RePlayApplication{

	public static void main(String[] args) {
		SpringApplication.run(RePlayApplication.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
	    RestTemplate restTemplate = new RestTemplate();
	    
	    // 1. JSON 변환을 담당하는 컨버터 생성
	    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
	    
	    // 2. iTunes가 던지는 javascript 타입을 JSON으로 인식하도록 설정
	    List<MediaType> supportedMediaTypes = new ArrayList<>();
	    supportedMediaTypes.add(MediaType.APPLICATION_JSON); // 기본 JSON
	    supportedMediaTypes.add(new MediaType("text", "javascript", StandardCharsets.UTF_8)); // iTunes용
	    supportedMediaTypes.add(new MediaType("application", "x-javascript", StandardCharsets.UTF_8));
	    
	    converter.setSupportedMediaTypes(supportedMediaTypes);
	    
	    // 3. RestTemplate의 컨버터 목록 제일 앞에 추가
	    restTemplate.getMessageConverters().add(0, converter);
	    
	    return restTemplate;
	}

}
