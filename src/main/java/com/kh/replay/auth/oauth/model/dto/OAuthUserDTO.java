package com.kh.replay.auth.oauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthUserDTO {
    private String memberId;        // DB PK
    private String provider;        // kakao, google
    private String providerId;      // 각 제공자가 내려준 id
    private String createdAt;       // 생성 날짜
    private String email;           // 회원가입 시 member 테이블 저장
    private String name;            // 회원 name
    private String nickName;        // 닉네임 (추가)
    private boolean profileCompleted; // 프로필 완료 여부
}