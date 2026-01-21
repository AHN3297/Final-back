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
	public int updatePlayListName(int playListId, String memberId, String newName) {
	    // 1. 매퍼에 전달할 데이터를 DTO나 Map에 담습니다.
	    // XML의 #{memberId}, #{playListId}, #{playListName}과 이름이 같아야 합니다.
	    PlayListDTO updateDto = PlayListDTO.builder()
	            .playListId(playListId)
	            .memberId(memberId)
	            .playListName(newName)
	            .build();

	    // 2. Mapper를 호출하여 업데이트 수행
	    int result = playListMapper.updatePlayListName(updateDto);

	    // 3. 만약 결과가 0이라면 타인의 리스트에 접근했거나 해당 ID가 없는 경우입니다.
	    if (result <= 0) {
	        throw new RuntimeException("수정 권한이 없거나 존재하지 않는 플레이리스트입니다.");
	    }

	    return result;
	}
}
