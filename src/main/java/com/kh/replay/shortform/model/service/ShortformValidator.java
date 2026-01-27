package com.kh.replay.shortform.model.service;

import org.springframework.stereotype.Component;

import com.kh.replay.global.exception.ForbiddenException;
import com.kh.replay.global.exception.ResourceNotFoundException;
import com.kh.replay.shortform.model.dao.ShortformMapper;
import com.kh.replay.shortform.model.dto.ShortformDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShortformValidator {

	private final ShortformMapper shortformMapper;

	public ShortformDTO validateExisting(Long shortFormId) {
		ShortformDTO shortform = shortformMapper.findByShortFormId(shortFormId);
		if (shortform == null) {
			throw new ResourceNotFoundException("해당 숏폼을 찾을 수 없습니다.");
		}
		return shortform;
	}

	public void validateOwner(ShortformDTO shortform, String userId) {
		if (!shortform.getMemberId().equals(userId)) {
			throw new ForbiddenException("해당 숏폼에 대한 권한이 없습니다.");
		}
	}

	public void validateSort(String sort) {
		if (!("latest".equals(sort) || "popular".equals(sort))) {
			throw new IllegalArgumentException("지원하지 않는 정렬 방식입니다. (latest, popular 중 선택)");
		}
	}

}