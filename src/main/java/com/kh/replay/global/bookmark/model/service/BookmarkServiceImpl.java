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
    // 변수명을 구체적으로 변경 (혼동 방지)
    private final UniverseBookmarkService universeBookmarkService; 
	

	@Override
	public BookmarkResponse bookmarkUniverse(Long universeId, String memberId) {
		
        // 1. 존재 확인
	    validator.validateExisting(universeId);
	    
        // 2. 유니버스 찜 서비스 호출 
	    universeBookmarkService.createBookmark(universeId, memberId); 
	    
        // 3. 카운트 조회
	    int totalBookmark = countBookmarks(universeId);
	    
	    return BookmarkResponse.builder()
	            .targetId(universeId)
	            .type("UNIVERSE") // 타입 명시
	            .isBookmark(true)
	            .bookmarkCount(totalBookmark)
	            .build();
	}
	
	@Override
	public BookmarkResponse unbookmarkUniverse(Long universeId, String memberId) {
        
        // 1. 존재 확인
	    validator.validateExisting(universeId);
	    
	    // 2. 유니버스 찜 서비스 호출
	    universeBookmarkService.deleteBookmark(universeId, memberId); 
	    
        // 3. 카운트 조회
	    int totalBookmark = countBookmarks(universeId);
	    
	    return BookmarkResponse.builder()
	            .targetId(universeId)
	            .type("UNIVERSE") // 타입 명시
	            .isBookmark(false)
	            .bookmarkCount(totalBookmark)
	            .build();
	}

    // 갯수 조회용 메소드
    private int countBookmarks(Long universeId) {
    	return bookmarkMapper.countBookmark(universeId);
    }

}