package com.kh.replay.auth.oauth.model.res;

import java.util.Map;

public class GoogleRes implements OAuth2Res {
	private final Map<String, Object> attributes;
	public GoogleRes(Map<String, Object> attributes) {
		if(attributes.get("email")==null) {
			throw new IllegalArgumentException("이메일 정보가 없습니다.");
		}
		this.attributes = attributes;
	}

	@Override
	public String getProvier() {
		return "google";
	}

	@Override
	public String getProviderId() {
		return attributes.get("sub").toString();
	}

	@Override
	public String getEmail() {
		return attributes.get("email").toString();
	}

	@Override
	public String getName() {
		return attributes.get("name").toString();
	}

}
