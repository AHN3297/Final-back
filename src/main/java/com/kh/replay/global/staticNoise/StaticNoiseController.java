package com.kh.replay.global.staticNoise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
//브라우저가 자동으로 요청하는 정적 리소스 (favicon,chrome devtools 설정 등 ) 를 조용히 처리하기 위한  컨트롤러
//실제 기능과는 무관한 요청으로 , 리소스를 제공하지 않으면 NoResourceFoundException로그가 반 복 출력됨

@RestController
public class StaticNoiseController {
	//브라우저 탭 아이콘 요청 처리
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build(); 
    }
    //크롬 devtools에서 자동으로 발생하는 설정 탐색 요청 처리
    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    public ResponseEntity<Void> chromeDevtools() {
        return ResponseEntity.noContent().build(); 
    }
}
