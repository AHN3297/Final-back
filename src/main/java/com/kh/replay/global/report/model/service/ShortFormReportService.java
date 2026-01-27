package com.kh.replay.global.report.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.report.model.dto.ReportRequestDTO;
import com.kh.replay.global.report.model.dto.ReportResponse;
import com.kh.replay.shortform.model.service.ShortformValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShortFormReportService {

    private final ReportService reportService;
    private final ShortformValidator shortformValidator;

    public ReportResponse reportShortform(Long shortFormId, String memberId, ReportRequestDTO request) {
        
        // 1. 숏폼 존재 확인
        shortformValidator.validateExisting(shortFormId);

        return reportService.createReport("SHORTFORM", shortFormId, memberId, request);
    }
}