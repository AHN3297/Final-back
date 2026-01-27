package com.kh.replay.global.report.model.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper {

	//중복신고
    int checkReport(Map<String, Object> reportMap);

    //신고등록
    void insertReport(Map<String, Object> reportMap);

}