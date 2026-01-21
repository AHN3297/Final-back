package com.kh.replay.auth.token.model.vo;

import java.util.Date;


import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RefreshToken {
	private int tokenId;
	private String token;
	private Date expiration;
	private Date createdAt;
	private String memberId;
	
}
