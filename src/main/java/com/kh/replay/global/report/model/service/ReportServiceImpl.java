package com.kh.replay.global.report.model.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.exception.DuplicateException;
import com.kh.replay.global.report.model.dao.ReportMapper;
import com.kh.replay.global.report.model.dto.ReportRequestDTO;
import com.kh.replay.global.report.model.dto.ReportResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    @Override
    public ReportResponse createReport(String type, Long targetId, String memberId, ReportRequestDTO request) {
        
     
        Map<String, Object> reportMap = new HashMap<>();
        reportMap.put("type", type);          
        reportMap.put("targetId", targetId);  
        reportMap.put("memberId", memberId);  

        // 1. 중복 신고 확인
        if (reportMapper.checkReport(reportMap) > 0) {
            throw new DuplicateException("중복신고입니다.");
        }

        // 2. 신고 내용 추가		
        reportMap.put("reason", request.getReason());
        reportMap.put("description", request.getDescription());

        reportMapper.insertReport(reportMap);

        
        Long generatedId = (Long) reportMap.get("reportId");

        // 6. 응답 DTO 반환
        return ReportResponse.builder()
                .reportId(generatedId)
                .targetId(targetId)
                .type(type)
                .reason(request.getReason())
                .description(request.getDescription())
                .reportAt(LocalDateTime.now())
                .build();
    }
}