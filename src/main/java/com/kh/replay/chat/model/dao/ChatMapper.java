package com.kh.replay.chat.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.replay.chat.model.vo.ChatVO;

@Mapper // MyBatis 매퍼임을 명시
public interface ChatMapper {

    /**
     * 채팅 메시지 저장
     * @param chat (memberId, chatContent 포함)
     * @return 성공 시 1
     */
    int insertChat(ChatVO chat);

    /**
     * 최근 3일치 채팅 내역 조회
     * @return 닉네임이 포함된 ChatVO 리스트
     */
    List<ChatVO> selectRecentChats();

    /**
     * 3일 지난 채팅 내역 삭제 (스케줄러용)
     * @return 삭제된 행의 수
     */
    int deleteOldChats();
}