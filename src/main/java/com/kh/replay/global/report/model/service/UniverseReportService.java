package com.kh.replay.global.report.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.report.model.dto.ReportRequestDTO;
import com.kh.replay.global.report.model.dto.ReportResponse;
import com.kh.replay.universe.model.service.UniverseValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UniverseReportService {

    private final ReportService reportService;
    private final UniverseValidator universeValidator; 

    public ReportResponse reportUniverse(Long universeId, String memberId, ReportRequestDTO request) {
        
        // 1. 존재 확인
        universeValidator.validateExisting(universeId);

        return reportService.createReport("UNIVERSE", universeId, memberId, request);
    }
}