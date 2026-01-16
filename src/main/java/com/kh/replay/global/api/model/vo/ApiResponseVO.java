package com.kh.replay.global.api.model.vo;

import java.util.List;
import lombok.Data;

@Data
public class ApiResponseVO {
    private int resultCount;      // 결과 개수
    private List<ItemWrapper> results; // 실제 데이터 리스트
}