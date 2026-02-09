package com.kh.replay.member.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kh.replay.auth.oauth.model.dto.AdditionalInfoRequest;
import com.kh.replay.auth.oauth.model.dto.OAuthUserDTO;
import com.kh.replay.member.model.dto.GenreDTO;
import com.kh.replay.member.model.dto.MemberInfoDTO;
import com.kh.replay.member.model.dto.MemberUpdateRequest;

@Mapper
public interface MemberMapper {
	
	// 멤버 이메일로 조회
	Map<String, String> loadByMemberEmail(String email);
	
	@Update("UPDATE TB_LOCAL SET PASSWORD = #{newPassword} WHERE MEMBER_ID = #{memberId}")
	int changePassword(Map<String, String> changeRequest);
	
	// 회원 상세 정보 조회
	List<MemberInfoDTO> findAllInfo(String memberId);
	
	// 회원 정보 수정
	int changeInfo(MemberUpdateRequest request);
	
	// 회원 정보 수정 시 장르를 삭제
	@Delete("DELETE FROM TB_MEMBER_GENRE WHERE MEMBER_ID = #{memberId}")
	void deleteMemberGenres(String memberId);
	
	// 회원 장르 추가
	int insertMemberGenres(@Param("memberId") String memberId, @Param("genreIds") List<Long> genres);
	
	@Update("UPDATE TB_MEMBER SET STATUS = 'N' WHERE MEMBER_ID = #{memberId}")
	void withdrawMember(String memberId);
	
	void insertOAuthBasicInfo(OAuthUserDTO oauthUser);
	
	@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE EMAIL = #{email}")
	boolean existByEmail(String email);
	
	void updateCompleteMember(AdditionalInfoRequest request);
	
	Map<String, String> loadSocialUser(String memberId);
	
	@Update("UPDATE TB_MEMBER SET STATUS = 'N' WHERE MEMBER_ID = #{memberId}")
	void withdrawSocial(String memberId);
	
	@Select("SELECT CASE WHEN EXISTS(SELECT 1 FROM TB_LOCAL L JOIN TB_MEMBER M ON M.MEMBER_ID = L.MEMBER_ID WHERE M.EMAIL = #{email}) THEN 1 ELSE 0 END FROM DUAL")
	boolean existsLocalByEmail(String email);
	
	String findMemberIdByEmail(String email);
	
	Map<String, String> findByMemberId(String memberId);
	
	Map<String, String> findUserByMemberId(String memberId);
	
	List<GenreDTO> findAllGenres();
}