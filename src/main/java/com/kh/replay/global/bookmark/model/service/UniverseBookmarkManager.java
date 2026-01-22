package com.kh.replay.global.bookmark.model.service;


import org.springframework.stereotype.Component;

import com.kh.replay.global.bookmark.model.dao.BookmarkMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UniverseBookmarkManager {

    private final BookmarkMapper bookmarkMapper;
    
    public void createBookmark(Long universeId, String memberId) {
        if (bookmarkMapper.checkBookmark(universeId, memberId) == 0) {
        	bookmarkMapper.insertBookmark(universeId, memberId);
        }
    }
}