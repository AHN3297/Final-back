package com.kh.replay.api.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.api.model.service.ApiService;
import com.kh.replay.global.common.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@ResponseBody
@RequestMapping("api")
@RequiredArgsConstructor
public class ApiController {

    private final ApiService service;

    /**
     * 노래 / 아티스트 검색 (페이징 정보 포함 응답)
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseData<Map<String, Object>>> findAllSearch(
        @RequestParam("keyword") String keyword,
        @RequestParam("category") String category,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Map<String, Object> searchResult = service.findAllByKeyword(keyword, category, page, size);

        return ResponseData.ok(searchResult, "노래/아티스트 조회 성공");
    }
}