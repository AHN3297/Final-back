package com.kh.replay.global.bookmark.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kh.replay.global.bookmark.model.dao.BookmarkMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UniverseBookmarkService {

    private final BookmarkMapper bookmarkMapper;
    
    public void createBookmark(Long universeId, String memberId) {
        // Mapper에 전달할 Map 생성
        Map<String, Object> params = new HashMap<>();
        params.put("universeId", universeId);
        params.put("memberId", memberId);

        // Map을 이용하여 중복 체크 및 insert 수행
        if (bookmarkMapper.checkBookmark(params) == 0) {
            bookmarkMapper.insertBookmark(params);
        }
    }

    public void deleteBookmark(Long universeId, String memberId) {
        // Mapper에 전달할 Map 생성
        Map<String, Object> params = new HashMap<>();
        params.put("universeId", universeId);
        params.put("memberId", memberId);

        // Map을 이용하여 존재 확인 및 delete 수행
        if (bookmarkMapper.checkBookmark(params) > 0) {
            bookmarkMapper.deleteBookmark(params);
        }
    }
}