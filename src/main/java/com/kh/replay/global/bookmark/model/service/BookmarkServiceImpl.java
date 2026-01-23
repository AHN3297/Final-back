package com.kh.replay.global.bookmark.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.bookmark.model.dao.BookmarkMapper;
import com.kh.replay.global.bookmark.model.dto.BookmarkResponse;
import com.kh.replay.universe.model.service.UniverseValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkServiceImpl implements BookmarkService {
	
    private final BookmarkMapper bookmarkMapper;
    private final UniverseValidator validator;
    private final UniverseBookmarkService bookmarkService; 
	

	@Override
	public BookmarkResponse bookmarkUniverse(Long universeId, String memberId) {
		
        // 1. 존재 확인
	    validator.validateExisting(universeId);
	    
        // 2. 매니저 호출 
	    bookmarkService.createBookmark(universeId, memberId); 
	    
        // 3. 카운트 조회 (Long 타입 그대로 전달)
	    int totalBookmark = countBookmarks(universeId);
	    
	    return BookmarkResponse.builder()
	            .targetId(universeId)
	            .isBookmark(true)
	            .bookmarkCount(totalBookmark)
	            .build();
	}
	
	@Override
	public BookmarkResponse unbookmarkUniverse(Long universeId, String memberId) {
        
        // 1. 존재 확인
	    validator.validateExisting(universeId);
	    
	    // 2. 매니저 호출
	    bookmarkService.deleteBookmark(universeId, memberId); 
	    
        // 3. 카운트 조회
	    int totalBookmark = countBookmarks(universeId);
	    
	    return BookmarkResponse.builder()
	            .targetId(universeId)
	            .isBookmark(false)
	            .bookmarkCount(totalBookmark)
	            .build();
	}

    // 갯수 조회용 메소드
    private int countBookmarks(Long universeId) {
    	return bookmarkMapper.countBookmark(universeId);
    }

}