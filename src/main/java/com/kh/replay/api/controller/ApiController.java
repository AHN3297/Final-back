package com.kh.replay.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.api.model.dto.ApiResponseDTO;
import com.kh.replay.api.model.service.ApiService;

import lombok.RequiredArgsConstructor;

@RestController
@ResponseBody
@RequestMapping("api")
@RequiredArgsConstructor
public class ApiController {
	private final ApiService service;
	
	@GetMapping("/search")
	public String findAllSearch(@RequestParam("keyword") String ketword,
			                    @RequestParam("category") String category,
			                    @RequestParam(value = "page", defaultValue = "0") int page,
			                    @RequestParam(value = "size", defaultValue = "10") int size) {
		List<ApiResponseDTO> results = service.getMusicData(ketword, category, page, size);
		
		return null;
		
	}

}
