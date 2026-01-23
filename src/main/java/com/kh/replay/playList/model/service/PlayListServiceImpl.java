package com.kh.replay.playList.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.api.model.dto.MusicDTO;
import com.kh.replay.global.exception.ForbiddenException;
import com.kh.replay.global.exception.NotFoundOrderListException;
import com.kh.replay.playList.model.dao.PlayListMapper;
import com.kh.replay.playList.model.dto.PlayListDTO;
import com.kh.replay.playList.model.dto.UpdateOrderDTO;
import com.kh.replay.playList.model.vo.PlayListTrackVO;
import com.kh.replay.playList.model.vo.PlayListVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayListServiceImpl implements PlayListService {

    private final PlayListMapper playListMapper;

    
     // 플레이리스트 소유권 검증 메서드
    private void verifyMemberId(int playListId, String memberId) {
        int count = playListMapper.checkPlayListOwnership(playListId, memberId);
        if (count <= 0) {
            log.warn("권한 없는 접근 차단 - 사용자: {}, 플레이리스트ID: {}", memberId, playListId);
            throw new ForbiddenException("해당 플레이리스트에 대한 접근 권한이 없습니다.");
        }
    }

    // 1. 플레이리스트 생성
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createPlayList(PlayListDTO playListDto, String memberId) {
        PlayListVO vo = PlayListVO.builder()
                .playListName(playListDto.getPlayListName())
                .memberId(memberId)
                .build();       
        int result = playListMapper.createPlayList(vo);               
        if (result <= 0) {
            throw new IllegalArgumentException("플레이리스트 등록에 실패했습니다.");
        }      
        return vo.getPlayListId(); 
    }

    // 2. 플레이리스트 목록 조회 (본인 ID로만 조회하므로 안전)
    @Override
    @Transactional(readOnly = true) 
    public List<PlayListDTO> findAllMemberPlayLists(String memberId) {
    	List<PlayListDTO> playList = playListMapper.findAllMemberPlayLists(memberId);
    	
    	// 방어목적
    	if(playList == null) {
    		return new ArrayList<>();
    	}
    	
    	return playList;
    }

    // 3. 메인 플레이리스트 지정
    @Override
    @Transactional(rollbackFor = Exception.class) 
    public int updateMainPlayList(String memberId, int playListId) {

        verifyMemberId(playListId, memberId);

        playListMapper.deleteMainPlayList(memberId);
        int result = playListMapper.createMainPlayList(memberId, playListId);
        
        if (result <= 0) {
            throw new RuntimeException("메인 플레이리스트 설정에 실패했습니다.");
        }
        return result;
    }

    // 4. 플레이리스트 이름 수정
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePlayListName(int playListId, String memberId, String newName) {
    	
        verifyMemberId(playListId, memberId);

        PlayListDTO updateDto = PlayListDTO.builder()
                .playListId(playListId)
                .memberId(memberId)
                .playListName(newName)
                .build();
        
        int result = playListMapper.updatePlayListName(updateDto);
        if (result <= 0) {
            throw new IllegalArgumentException("이름 수정에 실패했습니다.");
        }
        return result;
    }

    // 5. 플레이리스트 삭제
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePlayList(int playListId, String memberId) {
        // [검증 호출]
        verifyMemberId(playListId, memberId);

        int result = playListMapper.deletePlayList(playListId, memberId);       
        if (result <= 0) {
            throw new RuntimeException("플레이리스트 삭제에 실패했습니다.");
        }
        return result;
    }

    // 6. 플레이리스트에 노래 추가
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createPlayListSong(MusicDTO musicDto, int playListId, String memberId) {

        verifyMemberId(playListId, memberId);
        
        // DURATION 변환 
        String formattedDuration = "00:00";
        if (musicDto.getDuration() != null) {
            long totalSeconds = musicDto.getDuration() / 1000;
            formattedDuration = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
        }

        PlayListTrackVO trackVo = PlayListTrackVO.builder()
                .trackId(musicDto.getTrackId())
                .artistId(musicDto.getArtistId())
                .title(musicDto.getTitle())
                .duration(formattedDuration)
                .artistName(musicDto.getArtistName())
                .album(musicDto.getAlbum())
                .genreName(musicDto.getGenreName())
                .coverImgUrl(musicDto.getCoverImgUrl())
                .previewUrl(musicDto.getPreviewUrl())
                .releaseDate(musicDto.getReleaseDate())
                .build();

        try {
            playListMapper.createTrack(trackVo); //
        } catch (Exception e) {
            throw new RuntimeException("곡 정보 저장 중 서버 오류가 발생했습니다.");
        }
        int nextOrder = playListMapper.getNextTrackOrder(playListId);
        int result = playListMapper.insertPlaylistSong(playListId, trackVo.getSongNo(), nextOrder); //

        if (result <= 0) {
            throw new RuntimeException("플레이리스트에 곡을 담지 못했습니다.");
        }

        return result;
    }
    
    // 플레이리스트 곡 조회
    @Override
    @Transactional(readOnly = true)
    public List<PlayListTrackVO> getPlaylistTracks(int playListId, String memberId) {
        verifyMemberId(playListId, memberId);
        
        return playListMapper.selectPlaylistTracks(playListId, memberId);
    }
    
   // 플레이리스트 내 곡 순서 변경
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTrackOrder(int playListId, String memberId, List<UpdateOrderDTO> orderList) {

        verifyMemberId(playListId, memberId);
        
        if(orderList == null || orderList.isEmpty()) {
        	throw new NotFoundOrderListException("변경할 순서 정보가 없습니다.");
        }

        int totalResult = 0;
        // 리스트를 돌며 순서 업데이트
        try {
	        for (UpdateOrderDTO dto : orderList) {
	            int result = playListMapper.updateTrackOrder(dto);
	            if(result == 0) {
	            	throw new IllegalStateException("순서 변경 중 오류가 발생하였습니다.");
	            }
	        }	
        } catch (Exception e) {
        	throw new IllegalStateException("순서 변경 중 오류가 발생하였습니다.");
        }
        
        return totalResult;
    }

    // 플레이리스트 내 곡 삭제
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int deletePlaylistTracks(int playListId, int songId, String memberId) {
		
		verifyMemberId(playListId, memberId);
		
		int result = playListMapper.deletePlaylistTracks(playListId, songId, memberId);
		if(result<= 0) {
			throw new IllegalStateException("노래삭제에 실패했습니다.");
		}
		return result;
	}
}