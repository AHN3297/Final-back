package com.kh.replay.auth.oauth.model.dto;

import lombok.Data;

@Data
public class AdditionalInfoRequest {
	private String memberId;
	private String nickName;
	private String phone;
	private String mbti;
	private String job;
	private String genre;
	private String gender;
	private String email;
	private String name;
}
