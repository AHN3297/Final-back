package com.kh.replay.global.bookmark.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.bookmark.model.dao.BookmarkMapper;
import com.kh.replay.global.bookmark.model.dto.BookmarkResponse; // DTO 임포트 확인
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
    private final UniverseBookmarkManager bookmarkManager; 
	

	@Override
	public BookmarkResponse bookmarkUniverse(Long universeId, String memberId) {
		
	    validator.validateExisting(universeId);
	    
	    bookmarkManager.createBookmark(universeId, memberId); 
	    
	    int totalBookmark = countBookmarks(universeId);
	    
	    return BookmarkResponse.builder()
	            .targetId(universeId)
	            .isBookmark(true) // 무조건 true
	            .bookmarkCount(totalBookmark)
	            .build();
	}



    // 갯수 조회용 메소드
    private int countBookmarks(Long universeId) {
    	return bookmarkMapper.countBookmark(universeId);
    }

}