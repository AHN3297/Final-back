package com.kh.replay.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.kh.replay.chat.model.service.ChatService;
import com.kh.replay.chat.model.vo.ChatVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatVO sendMessage(@Payload ChatVO chat) {
        // 1. DB 저장 (nickname은 보통 세션에서 꺼내서 세팅)
        chatService.saveAndGetMessage(chat);
        // 2. 브로드캐스팅
        return chat;
    }
}