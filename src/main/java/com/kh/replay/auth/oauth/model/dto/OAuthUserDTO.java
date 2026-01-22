package com.kh.replay.auth.oauth.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthUserDTO {
	private String memberId; //DB PK
	private String provider; //kakao ,google
	private String providerId; //각 제공자가 내려준 id
	private String createdAt; //생성 날짜
	
}
