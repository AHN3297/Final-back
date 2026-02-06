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
        chatMapper.insertChat(chat);
        return chat; 
    }
}