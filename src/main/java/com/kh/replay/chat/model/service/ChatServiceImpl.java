package com.kh.replay.chat.model.service;

import org.springframework.stereotype.Service;

import com.kh.replay.chat.model.dao.ChatMapper;
import com.kh.replay.chat.model.vo.ChatVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatMapper chatMapper;

    @Override
    public ChatVO saveAndGetMessage(ChatVO chat) {
        // 1. 저장
        chatMapper.insertChat(chat);
        // 2. 저장된 후 닉네임이나 포맷팅된 시간을 포함해 다시 가공하거나 
        // 아예 보낼 데이터를 맵퍼에서 다시 조회해와도 됩니다.
        return chat; 
    }
}