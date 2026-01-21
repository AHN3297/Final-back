package com.kh.replay.auth.member.model.vo;

import java.sql.Date;

import lombok.Builder;
import lombok.Value;
@Builder
@Value
public class MemberVO {
private String memberId;
private String email;
private String name;
private String nickName;
private String phone;
private String gender;
private String genre;
private String job;
private String mbti;
private Date createdAt;
private Date updatedAt;
private String role;
private String status;

}