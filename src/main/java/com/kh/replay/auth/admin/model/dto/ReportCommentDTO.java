package com.kh.replay.auth.admin.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportCommentDTO {
	private int reportId;
	private String reasonCode;
	private String description;
	private char status;
	private Date createdAt;
	private Date updatedAt;
	private int commentId;
	private String memberid;

}
