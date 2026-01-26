package com.kh.replay.auth.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data

@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

	private int offset;
	private int size;
}
