package com.kh.replay.playList.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.playList.model.dao.PlayListMapper;
import com.kh.replay.playList.model.dto.PlayListDTO;
import com.kh.replay.playList.model.vo.PlayListVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayListServiceImpl implements PlayListService {

    private final PlayListMapper playListMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) // 생성 중 에러 발생 시 롤백
    public int createPlayList(PlayListDTO playListDto, String memberId) {
        PlayListVO vo = PlayListVO.builder()
                .playListName(playListDto.getPlayListName())
                .memberId(memberId)
                .build();       
        int result = playListMapper.createPlayList(vo);            
        if (result <= 0) {
            throw new RuntimeException("플레이리스트 등록에 실패했습니다.");
        }      
        return vo.getPlayListId(); 
    }

    @Override
    @Transactional(readOnly = true) 
    public List<PlayListDTO> findAllMemberPlayLists(String memberId) {
        return playListMapper.findAllMemberPlayLists(memberId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) 
    public int updateMainPlayList(String memberId, int playListId) {
        // 1. 기존 메인플레이리스트 설정 해제
        playListMapper.deleteMainPlayList(memberId);
        
        // 2. 새로운 메인플레이리스트 등록
        int result = playListMapper.createMainPlayList(memberId, playListId);
        
        // 만약 등록에 실패하면 이전 삭제 건도 롤백되어야 함
        if (result <= 0) {
            throw new RuntimeException("메인 플레이리스트 설정에 실패했습니다.");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePlayListName(int playListId, String memberId, String newName) {
        PlayListDTO updateDto = PlayListDTO.builder()
                .playListId(playListId)
                .memberId(memberId)
                .playListName(newName)
                .build();
        int result = playListMapper.updatePlayListName(updateDto);
        if (result <= 0) {
            throw new RuntimeException("수정 권한이 없거나 존재하지 않는 플레이리스트입니다.");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePlayList(int playListId, String memberId) {
        int result = playListMapper.deletePlayList(playListId, memberId);        
        if (result <= 0) {
            throw new RuntimeException("삭제 권한이 없거나 존재하지 않는 플레이리스트입니다.");
        }
        return result;
    }
}