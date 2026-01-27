package com.kh.replay.auth.admin.model.service;

import java.util.Date;
import java.util.Map;

public interface AdminService {


	Map<String, Object> memberList(int page, int size);


	void getDashboardSummary(int totalAccount, int admins, int user, Date asOf, int withdrawnAccounts);

}
