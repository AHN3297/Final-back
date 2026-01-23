package com.kh.replay.global.bookmark.model.service;

import com.kh.replay.global.bookmark.model.dto.BookmarkResponse;

public interface BookmarkService {

	BookmarkResponse bookmarkUniverse(Long universeId, String username);

}