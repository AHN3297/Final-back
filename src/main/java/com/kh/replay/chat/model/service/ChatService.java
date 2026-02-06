package com.kh.replay.chat.model.service;

import com.kh.replay.chat.model.vo.ChatVO;

public interface ChatService {

	ChatVO saveAndGetMessage(ChatVO chat);

}
