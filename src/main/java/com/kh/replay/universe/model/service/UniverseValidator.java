package com.kh.replay.universe.model.service;

import org.springframework.stereotype.Component;

import com.kh.replay.global.exception.ForbiddenException;
import com.kh.replay.global.exception.ResourceNotFoundException;
import com.kh.replay.universe.model.dao.UniverseMapper;
import com.kh.replay.universe.model.dto.UniverseDTO;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class UniverseValidator {

    private final UniverseMapper universeMapper;

    /**
     * 유니버스 존재 여부 확인
     * @return UniverseDTO 아니면 예외
     */
    public UniverseDTO validateExisting(Long universeId) {
        UniverseDTO universe = universeMapper.findByUniverseId(universeId);
        if (universe == null) {
            throw new ResourceNotFoundException("해당 유니버스를 찾을 수 없습니다.");
        }
        return universe;
    }

    /**
     * 유니버스 소유권(권한) 확인
     * @param universe 
     * @param userId 아니면 예외
     */
    public void validateOwner(UniverseDTO universe, String userId) {
        if (!universe.getMemberId().equals(userId)) {
            throw new ForbiddenException("해당 유니버스에 대한 권한이 없습니다.");
        }
    }

    /**
     * 정렬 조건 유효성 검사
     * 리턴 없음 아니면 예외
     */
    public void validateSort(String sort) {
        if (!("latest".equals(sort) || "popular".equals(sort))) {
            throw new IllegalArgumentException("지원하지 않는 정렬 방식입니다. (latest, popular 중 선택)");
        }
    }
}