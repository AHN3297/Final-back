package com.kh.replay.global.like.model.dao; 

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.replay.global.like.model.vo.LikeArtistVO; 
import com.kh.replay.global.like.model.vo.LikeGenreVO;
import com.kh.replay.global.like.model.vo.LikeTrackVO; 

@Mapper
public interface LikeMapper {

    // 유니버스 좋아요
    int checkLike(Map<String, Object> params);
    int insertLike(Map<String, Object> params);
    int deleteLike(Map<String, Object> params);
    int countUniverseLikes(Long universeId);

    // ==========================================
    // [Upstream] 가수, 장르, 노래 관련 좋아요 기능
    // ==========================================

    // apiId로 좋아하는 가수 조회
    Integer findSingerNoByApiId(Long apiSingerId);
    
    // 가수 정보 insert
    void insertArtistApiInfo(LikeArtistVO artistVo);
    
    // 가수 중복 조회
    int checkArtistLikeExists(Map<String, Object> params);
    
    // 좋아하는 가수 insert (중간테이블)
    int insertFavoriteArtist(Map<String, Object> params);
    
    // 좋아하는 가수 삭제 (중간테이블만 삭제)
    int deleteArtistLike(Map<String, Object> params);
   
    // 좋아하는 가수 조회
    List<LikeArtistVO> selectFavoriteArtists(String memberId);
    
    // 장르 선택 인터페이스
    Long findGenreIdByName(String genreName);
    
    int existsMemberGenre(@Param("memberId") String memberId, 
    					  @Param("genreId") Long genreId);
    
    int insertMemberGenre(LikeGenreVO vo);
    
    // 노래 중복 확인
	Long selectSongNoByApiId(Long track);
	
	// 좋아하는 노래 중복 확인
	int checkSongLikeExists(@Param("memberId") String memberId, @Param("songNo") Long songNo);
	
	// 좋아하는 노래 정보 추가
	int insertApiSong(LikeTrackVO trackVO);
	
	// 좋아하는 노래 추가
	int insertFavoriteSong(@Param("memberId") String memberId, 
			                @Param("songNo") Long songNo);

	List<LikeTrackVO> selectFavoriteTracks(String memberId);

	int deleteFavoriteSong(@Param("memberId") String memberId, @Param("songNo") Long songNo);

	int updateTrackOrder(@Param("memberId") String memberId, @Param("songNo") Long songNo, @Param("newOrder") int newOrder);



    // 숏폼 좋아요 여부 확인
    int checkShortformLike(Map<String, Object> params);
    
    // 숏폼 좋아요 추가
    int insertShortformLike(Map<String, Object> params);
    
    // 숏폼 좋아요 취소
    int deleteShortformLike(Map<String, Object> params);
    
    // 숏폼 좋아요 개수 카운트
    int countShortformLikes(Long shortFormId);

}