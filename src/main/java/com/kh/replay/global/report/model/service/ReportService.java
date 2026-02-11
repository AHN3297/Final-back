package com.kh.replay.global.report.model.service;

import com.kh.replay.global.report.model.dto.ReportRequestDTO;
import com.kh.replay.global.report.model.dto.ReportResponse;

public interface ReportService {

    ReportResponse createReport(String type, Long targetId, String memberId, ReportRequestDTO request);

}