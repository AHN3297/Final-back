package com.kh.replay.auth.admin.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryDTO {

	private int totalAccount;
	private int admins;
	private int user;
	private int withdrawnAccounts;
	private Date asOf;
}
