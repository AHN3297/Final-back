package com.kh.replay.auth.admin.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDetailDTO {
	private String reportType;     // SHORT_FORM | COMMENT | UNIVERSE
    private Long reportNo;

    private String reasonCode;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    private String memberId;        // 신고자

    private Long targetId;          // SHORTS_ID / COMMENT_ID / UNIVERSE_ID
	
	
	
	
	

}
