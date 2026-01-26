package com.kh.replay.global.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequestDTO {

    private String reason; // 신고사유 REASON_CODE
    private String description; // 상세 내용 DESCRIPTION


}