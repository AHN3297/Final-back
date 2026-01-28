package com.kh.replay.auth.admin.model.dto;

import com.kh.replay.global.api.model.dto.ArtistDTO;
import com.kh.replay.global.api.model.dto.MusicDTO;
import com.kh.replay.member.model.dto.MemberDTO;
import com.kh.replay.playList.model.dto.PlayListDTO;
import com.kh.replay.shortform.model.dto.ShortformDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Setter
@Getter
public class MemberDetailDTO {
	private MemberDTO member;
	private ShortformDTO shortform;
	private PlayListDTO playList;
	private ArtistDTO artist;
	private MusicDTO music;
	

}
