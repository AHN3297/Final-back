package com.kh.replay.auth.admin.model.service;

import java.util.Date;
import java.util.Map;

import com.kh.replay.auth.admin.model.dto.DashboardSummaryDTO;
import com.kh.replay.auth.admin.model.dto.MemberStatusRatio;

public interface AdminService {

	Map<String, Object> memberList(int page, int size);

	DashboardSummaryDTO getDashboardSummary();

	MemberStatusRatio getMemberStatusRatio();

}
