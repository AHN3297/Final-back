package com.kh.replay.auth.admin.model.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.replay.auth.admin.model.dao.AdminMapper;
import com.kh.replay.auth.admin.model.dto.DashboardSummaryDTO;
import com.kh.replay.auth.admin.model.dto.MemberDetailDTO;
import com.kh.replay.auth.admin.model.dto.MemberStatusRatio;
import com.kh.replay.auth.admin.model.dto.PageRequestDTO;
import com.kh.replay.auth.admin.model.dto.ReportCommentDTO;
import com.kh.replay.auth.admin.model.dto.ReportDetailDTO;
import com.kh.replay.auth.admin.model.dto.StatusInfo;
import com.kh.replay.global.exception.ReportNotFoundException;
import com.kh.replay.global.exception.ResourceNotFoundException;
import com.kh.replay.global.exception.UpdateFailedException;
import com.kh.replay.global.exception.UserNotFoundException;
import com.kh.replay.global.util.PageInfo;
import com.kh.replay.global.util.Pagenation;
import com.kh.replay.member.model.dao.MemberMapper;
import com.kh.replay.member.model.dto.MemberDTO;
import com.kh.replay.shortform.model.dao.ShortformMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {
	private final AdminMapper adminMapper;
	private final Pagenation pagenation;
	private final MemberMapper memberMapper;
	private final ShortformMapper shorformtMapper;

	@Override
	public Map<String, Object> searchMemberList(int page, int size, String keyword) {

		int totalElements = adminMapper.totalMemberCount(null);

		// pageInfo 생성
		PageInfo pageInfo = pagenation.getPageInfo(totalElements, page, // currentPage
				size, // 페이지당 회원 수
				10 // 페이지 네비게이션 개수
		);
		PageRequestDTO pageRequest = new PageRequestDTO((page - 1) * size, size, null);

		// 회원 목록 조회
		List<MemberDTO> items = adminMapper.searchMemberList(pageRequest);

		// Map형태로 담아서
		Map<String, Object> response = new HashMap<>();
		response.put("page", page);
		response.put("size", size);
		response.put("totalElements", totalElements);
		response.put("totalPages", pageInfo.getMaxPage());
		response.put("startPage", pageInfo.getStartPage());
		response.put("endPage", pageInfo.getEndPage());
		response.put("items", items);

		return response;
		// 전체 회원 수 조회

	}

	@Override
	public DashboardSummaryDTO getDashboardSummary() {

		int totalAccount = adminMapper.getAllUsers();
		int admins = adminMapper.getAllAdmins();
		int user = adminMapper.getAllMembers();
		int withdrawnAccounts = adminMapper.getInactiveMembers();
		Date asOf = new Date();

		DashboardSummaryDTO dashboard = new DashboardSummaryDTO(totalAccount, admins, user, withdrawnAccounts, asOf);

		return dashboard;

	}

	@Override
	public MemberStatusRatio getMemberStatusRatio() {

		int active = adminMapper.getActiveMembers();
		int withdrawn = adminMapper.getInactiveMembers();

		int total = active + withdrawn;

		double activeRatio = (double) active / total * 100;
		double withdrawnRatio = (double) withdrawn / total * 100;

		return new MemberStatusRatio(

				new StatusInfo(active, activeRatio), new StatusInfo(withdrawn, withdrawnRatio)

		);

	}

	// 회원 상세 조회
	@Override
	public MemberDetailDTO getMemberDetails(String memberId) {
		int result = adminMapper.CountById(memberId);
		if (result <= 0) {
			throw new ResourceNotFoundException("존재하지 않는 회원입니다.");
		}
		MemberDetailDTO member = adminMapper.getMemberDetails(memberId);
		if (member == null) {
			throw new UserNotFoundException("회원을 찾을 수 없습니다.");
		}
		return member;

	}

	@Transactional
	@Override
	public MemberDTO ChangePermissions(MemberDTO member) {

		int result = adminMapper.CountById(member.getMemberId());

		if (result <= 0) {
			throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
		}

		int response = adminMapper.ChangePermissions(member);
		if (response <= 0) {
			throw new UpdateFailedException("수정을 하지 못했습니다.");
		}

		MemberDTO user = adminMapper.getMemberInfo(member.getMemberId());

		return user;

	}

	@Transactional
	@Override
	public MemberDTO withdrawUser(MemberDTO member) {

		int result = adminMapper.CountById(member.getMemberId());

		if (result <= 0) {
			throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
		}

		int response = adminMapper.withdrawUser(member);

		if (response <= 0) {
			throw new UpdateFailedException("수정을 하지 못했습니다.");
		}

		MemberDTO user = adminMapper.getMemberInfo(member.getMemberId());

		return user;
	}

	@Override
	public Map<String, Object> findReportList(int page, int size, String keyword) {

		String kw = (keyword == null) ? null : keyword.trim();
		String likeKw = (kw == null || kw.isEmpty()) ? null : "%" + kw + "%";

		int totalElements = adminMapper.totalCounted(likeKw);
		// pageInfo 생성
		PageInfo pageInfo = pagenation.getPageInfo(totalElements, page, // currentPage
				size, // 페이지당 회원 수
				10 // 페이지 네비게이션 개수
		);
		PageRequestDTO pageRequest = new PageRequestDTO((page - 1) * size, size, likeKw);

		List<ReportCommentDTO> items = adminMapper.findReportList(pageRequest);

		Map<String, Object> response = new HashMap<>();
		response.put("page", page);
		response.put("size", size);
		response.put("totalElements", totalElements);
		response.put("totalPages", pageInfo.getMaxPage());
		response.put("startPage", pageInfo.getStartPage());
		response.put("endPage", pageInfo.getEndPage());
		response.put("items", items);

		return response;

	}

	@Override
	public ReportDetailDTO findReportDetail(Long reportNo) {
		int result = adminMapper.countByReport(reportNo);
		if (result <= 0) {
			throw new ReportNotFoundException("신고 내역을 찾을 수 없습니다.");
		}
		ReportDetailDTO reportDetail = adminMapper.getReportDetails(reportNo);
		
		return reportDetail;

	}

	@Override
	public void ChangeStatus(ReportDetailDTO reportDetail) {
	    int result = adminMapper.countByReport(reportDetail.getReportNo());
	    if (result <= 0) {
	        throw new ReportNotFoundException("신고 내역을 찾을 수 없습니다.");
	    }
	    
	    ReportDetailDTO existingReport = adminMapper.getReportDetails(reportDetail.getReportNo());
	    
	    if ("N".equals(existingReport.getStatus())) {
	        throw new IllegalStateException("이미 처리된 신고입니다.");
	    }
	    
	    int updateResult = 0;
	    switch (existingReport.getReportType()) {  
	        case "SHORT_FORM":
	            updateResult = adminMapper.updateShortFormReportStatus(reportDetail.getReportNo());
	            break;

	        case "COMMENT":
	            updateResult = adminMapper.updateCommentReportStatus(reportDetail.getReportNo());
	            break;

	        case "UNIVERSE":
	            updateResult = adminMapper.updateUniverseReportStatus(reportDetail.getReportNo());
	            break;

	        default:
	            throw new IllegalArgumentException("알 수 없는 신고 타입입니다: " + existingReport.getReportType());
	    }
	    
	    if (updateResult <= 0) {
	        throw new UpdateFailedException("신고 상태 변경에 실패했습니다.");
	    }
	}

}
