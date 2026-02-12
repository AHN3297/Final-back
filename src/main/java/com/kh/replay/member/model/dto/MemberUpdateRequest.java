package com.kh.replay.member.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class MemberUpdateRequest {
    private String memberId;
    private String nickName;
    private String phone;
    private String mbti;
    private String job;
    private String gender;
    private List<Long> genreIds;
}


