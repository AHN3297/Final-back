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
    @Transactional
    public int createPlayList(PlayListDTO playListDto, String memberId) {
        // 1. VO 객체 생성 (불변성 유지)
        PlayListVO vo = PlayListVO.builder()
                .playListName(playListDto.getPlayListName())
                .memberId(memberId)
                .build();

        // 2. DB Insert 실행
        int result = playListMapper.createPlayList(vo);
        
        // 3. 예외처리
        if (result <= 0) {
            throw new RuntimeException("플레이리스트 등록에 실패했습니다.");
        }
        
        // 4. 성공 시 생성된 시퀀스 ID만 반환
        return vo.getPlayListId(); 
    }

	@Override
	public List<PlayListDTO> findAllMemberPlayLists(String memberId) {

		return playListMapper.findAllMemberPlayLists(memberId);
	}

	@Override
	public int updateMainPlayList(String memberId, int playListId) {
		// 기존 메인플레이리스트 삭제
		playListMapper.deleteMainPlayList(memberId);
		// 메인플레이리스트 입력
		int result = playListMapper.createMainPlayList(memberId, playListId);
		return result;
	}

	@Override
    @Transactional 
    public int updatePlayListName(int playListId, String newName) {
        PlayListDTO updateDto = PlayListDTO.builder()
                .playListId(playListId)
                .playListName(newName)
                .build();

        // Mapper 호출
        int result = playListMapper.updatePlayList(updateDto);

        // 수정 실패 시 예외 처리 (후에 변경 예정)
        if (result <= 0) {
            throw new RuntimeException("플레이리스트 이름 수정에 실패했습니다.");
        }

        return result;
    }
}
