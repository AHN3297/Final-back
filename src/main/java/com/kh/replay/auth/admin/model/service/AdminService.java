package com.kh.replay.auth.admin.model.service;

import java.util.Map;

import com.kh.replay.auth.admin.model.dto.DashboardSummaryDTO;
import com.kh.replay.auth.admin.model.dto.MemberDetailDTO;
import com.kh.replay.auth.admin.model.dto.MemberStatusRatio;
import com.kh.replay.member.model.dto.MemberDTO;
import com.kh.replay.member.model.vo.MemberVO;

public interface AdminService {

	Map<String, Object> searchMemberList(int page, int size,String keyword);

	DashboardSummaryDTO getDashboardSummary();

	MemberStatusRatio getMemberStatusRatio();

	MemberDetailDTO getMemberDetails(String memberId);

	MemberDTO ChangePermissions(MemberDTO member);

	MemberDTO withdrawUser(MemberDTO member);

	Map<String, Object> findReportList(int page, int size, String keyword);




}
