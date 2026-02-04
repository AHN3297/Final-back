package com.kh.replay.auth.admin.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kh.replay.auth.admin.model.dto.MemberDetailDTO;
import com.kh.replay.auth.admin.model.dto.PageRequestDTO;
import com.kh.replay.auth.admin.model.dto.ReportCommentDTO;
import com.kh.replay.auth.admin.model.dto.ReportDetailDTO;
import com.kh.replay.member.model.dto.MemberDTO;

@Mapper
public interface AdminMapper {
	@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE MEMBER_ID IS NOT NULL AND STATUS = 'Y' ")
	int totalMemberCount(Object object);

	List<MemberDTO> searchMemberList(PageRequestDTO pageRequest);

	
	@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE MEMBER_ID IS NOT NULL" )
	int getAllUsers();

	@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE MEMBER_ID IS NOT NULL AND ROLE = 'ROLE_ADMIN'")
	int getAllAdmins();

	@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE MEMBER_ID  IS NOT NULL AND ROLE = 'ROLE_USER'")
	int getAllMembers();

	@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE MEMBER_ID IS NOT NULL AND STATUS = 'N'")
	int getInactiveMembers();

	
	@Select("SELECT COUNT (*) FROM TB_MEMBER WHERE MEMBER_ID IS NOT NULL AND STATUS = 'Y'")
	int getActiveMembers();

	MemberDetailDTO getMemberDetails(String memberId);

	@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE MEMBER_ID = #{memberId}")
	int CountById(String memberId);

	
	@Update("UPDATE TB_MEMBER SET ROLE = #{role} , UPDATED_AT = SYSDATE WHERE MEMBER_ID = #{memberId}")
	int ChangePermissions(MemberDTO member);

	
	
	@Select("SELECT MEMBER_ID AS memberId ,MEMBER_NAME AS name ,MBTI AS mbti ,MEMBER_JOB AS job,GENDER AS gender,GENRE AS genre ,ROLE AS role , STATUS AS status ,NICKNAME AS nickName ,PHONE AS phone ,EMAIL AS email ,UPDATED_AT AS updatedAt,CREATED_AT AS createdAt FROM TB_MEMBER 	WHERE MEMBER_ID = #{memberId}")
	MemberDTO getMemberInfo(String memberId);

	@Update("UPDATE TB_MEMBER SET STATUS = 'N' ,UPDATED_AT = SYSDATE WHERE MEMBER_ID = #{memberId}")
	int withdrawUser(MemberDTO member);
	
	
	
	List<ReportCommentDTO> findReportList(PageRequestDTO pageRequest);
	
	
	@Select("SELECT COUNT(*) FROM TB_COMMENT_REPORT WHERE REPORT_ID IS NOT NULL AND STATUS = 'Y' AND (REPORT_ID LIKE #{keyword} OR DESCRIPTION LIKE #{keyword} OR REASON_CODE LIKE #{keyword})")
	int totalCounted(String likeKw);

	int countByReport(Long reportNo);
	
	
	
	ReportDetailDTO getReportDetails(Long reportNo);


	int updateShortFormReportStatus(Long reportNo);

	int updateCommentReportStatus(Long reportNo);

	int updateUniverseReportStatus(Long reportNo);

	

	


}
