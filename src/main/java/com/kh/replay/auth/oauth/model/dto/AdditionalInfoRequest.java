package com.kh.replay.auth.oauth.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class AdditionalInfoRequest {
	private String memberId;
	private String nickName;
	private String phone;
	private String mbti;
	private String job;
	private List<String> genre;
	private String gender;
	private String email;
	private String name;
}
