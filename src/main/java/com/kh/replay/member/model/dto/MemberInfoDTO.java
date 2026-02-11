package com.kh.replay.member.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//프로필 조회, 마이페이지 정보 표시 전용 DTO
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberInfoDTO {
	private  MemberDTO memberDto;
	private List<GenreDTO> genreDto;
	
	

}
