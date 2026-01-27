package com.kh.replay.global.report.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {

    private Long reportId; // 생성된 신고 고유 번호 (SEQ_REPORT_ID)
    private Long targetId; // 신고한 대상 ID (유니버스ID 등)
    private String type; // 대상 타입 ("UNIVERSE" 등)
    private String reason; // 신고 사유 코드
    private String description; // 상세 내용
    private LocalDateTime reportAt; // 신고 접수 시간

}