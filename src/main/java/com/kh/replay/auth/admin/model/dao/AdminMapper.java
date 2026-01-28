package com.kh.replay.auth.admin.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kh.replay.auth.admin.model.dto.MemberDetailDTO;
import com.kh.replay.auth.admin.model.dto.PageRequestDTO;
import com.kh.replay.member.model.dto.MemberDTO;

@Mapper
public interface AdminMapper {
	@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE MEMBER_ID IS NOT NULL AND STATUS = 'Y' ")
	int totalCount();

	List<MemberDTO> getMemberList(PageRequestDTO pageRequest);

	
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

	
	@Update("UPDATE TB_MEMBER SET ROLE = 'ROLE_USER' , UPDATED_AT = 'SYSDATE'")
	int ChangePermissions(MemberDTO member);

	


}
