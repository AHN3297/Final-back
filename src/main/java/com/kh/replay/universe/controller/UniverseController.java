package com.kh.replay.universe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import com.kh.replay.global.bookmark.model.dto.BookmarkResponse;
import com.kh.replay.global.bookmark.model.service.BookmarkService;
import com.kh.replay.global.common.ResponseData;
import com.kh.replay.global.like.model.dto.LikeResponse;
import com.kh.replay.global.like.model.service.LikeService;
import com.kh.replay.global.report.model.dto.ReportRequestDTO;
import com.kh.replay.global.report.model.dto.ReportResponse;
import com.kh.replay.global.report.model.service.ReportService;
import com.kh.replay.member.model.vo.CustomUserDetails;
import com.kh.replay.universe.model.dto.UniverseCreateRequest;
import com.kh.replay.universe.model.dto.UniverseDTO;
import com.kh.replay.universe.model.dto.UniverseListResponse;
import com.kh.replay.universe.model.service.UniverseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/universes")
public class UniverseController {
    
    private final UniverseService universeService;
    private final LikeService likeService;
    private final BookmarkService bookmarkService;
    private final ReportService reportService;
    

    /**
     * 1. 유니버스 목록 조회 (전체)
     */
    @GetMapping
    public ResponseEntity<ResponseData<UniverseListResponse>> findAllUniverse(
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @RequestParam(value = "lastUniverseId", required = false) Long lastUniverseId,
            @RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount
    ) {
        UniverseListResponse response = universeService.findAllUniverse(size, sort, lastUniverseId, lastLikeCount);
        return ResponseData.ok(response, "전체 조회 성공");
    }

    /**
     * 2. 유니버스 키워드 검색 조회
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseData<UniverseListResponse>> findByUniverse(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "condition", defaultValue = "all") String condition,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @RequestParam(value = "lastUniverseId", required = false) Long lastUniverseId,
            @RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount 
    ) {
        UniverseListResponse response = universeService.findByKeyword(keyword, condition, size, sort, lastUniverseId, lastLikeCount);
        return ResponseData.ok(response, "검색 조회 성공");
    }

    /**
     * 3. 유니버스 상세 조회 
     */
    @GetMapping("/{universeId}")
    public ResponseEntity<ResponseData<UniverseDTO>> findByUniverseId (
            @PathVariable("universeId") Long universeId
    ) {
        UniverseDTO response = universeService.findByUniverseId(universeId);
        return ResponseData.ok(response, "상세 조회 성공");
    }

    /**
     * 4. 유니버스 생성
     */
    @PostMapping
    public ResponseEntity<ResponseData<Void>> createUniverse(
            @Valid @RequestPart(value = "request") UniverseCreateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        universeService.createUniverse(request, file, user.getUsername());
        return ResponseData.ok(null, "유니버스 생성 성공");
    }
    
    /**
     * 5.유니버스 수정
     */
    @PatchMapping("{universeId}")
    public ResponseEntity<ResponseData<UniverseDTO>> updateUniverse(
            @PathVariable("universeId") Long universeId,
            @Valid @RequestBody UniverseCreateRequest universe,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UniverseDTO resonse =  universeService.updateUniverse(universeId, universe, user.getUsername());
        return ResponseData.ok(resonse, "유니버스 수정 성공");
    }
    

    /**
     * 6. 유니버스 삭제 
     */
    @DeleteMapping("/{universeId}")
    public ResponseEntity<ResponseData<UniverseDTO>> deleteUniverse(
            @PathVariable("universeId") Long universeId,
            @AuthenticationPrincipal CustomUserDetails user 
            ) {
        UniverseDTO response = universeService.deleteUniverse(universeId, user.getUsername());
        return ResponseData.ok(response, "유니버스 삭제 성공");
    }
    
    /**
     * 7. 유니버스 좋아요 (생성)
     */
    @PostMapping("/{universeId}/like")
    public ResponseEntity<ResponseData<LikeResponse>> likeUniverse(
            @PathVariable("universeId") Long universeId,
            @AuthenticationPrincipal CustomUserDetails user 
    ) {
        LikeResponse response = likeService.likeUniverse(universeId, user.getUsername());
        return ResponseData.ok(response, "좋아요를 눌렀습니다.");
    }

    /**
     * 8. 유니버스 좋아요 취소 (삭제)
     */
    @DeleteMapping("/{universeId}/like")
    public ResponseEntity<ResponseData<LikeResponse>> unlikeUniverse(
            @PathVariable("universeId") Long universeId,
            @AuthenticationPrincipal CustomUserDetails user 
    ) {
        LikeResponse response = likeService.unlikeUniverse(universeId, user.getUsername());
        return ResponseData.ok(response, "좋아요를 취소했습니다.");
    }
    
    /**
     * 9. 유니버스 북마크 (생성)
     */
    @PostMapping("/{universeId}/bookmark")
    public ResponseEntity<ResponseData<BookmarkResponse>> bookmarkUniverse(
            @PathVariable("universeId") Long universeId,
            @AuthenticationPrincipal CustomUserDetails user 
    ) {
        BookmarkResponse response = bookmarkService.bookmarkUniverse(universeId, user.getUsername());
        return ResponseData.ok(response, "찜하기를 눌렀습니다.");
    }
    
    /**
     * 10. 유니버스 북마크 취소 (삭제)
     */
    @DeleteMapping("/{universeId}/bookmark")
    public ResponseEntity<ResponseData<BookmarkResponse>> unbookmarkUniverse(
            @PathVariable("universeId") Long universeId,
            @AuthenticationPrincipal CustomUserDetails user 
    ) {
        BookmarkResponse response = bookmarkService.unbookmarkUniverse(universeId, user.getUsername());
        return ResponseData.ok(response, "찜하기를 취소했습니다.");
    }
    
    /**
     * 11. 유니버스 신고
     */
    @PostMapping("/{universeId}/report")
    public ResponseEntity<ResponseData<ReportResponse>> reportUniverse(
            @PathVariable("universeId") Long universeId,
            @RequestBody ReportRequestDTO request, 
            @AuthenticationPrincipal CustomUserDetails user 
    ) {
      
        ReportResponse response = reportService.createReport("UNIVERSE", universeId, user.getUsername(), request);
        return ResponseData.ok(response, "신고가 접수되었습니다.");
    }
    
    /**
     * 12. 찜한 유니버스 목록 조회
     */
    @GetMapping("/me/bookmarks")
    public ResponseEntity<ResponseData<UniverseListResponse>> findBookmarkedUniverses(
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "lastUniverseId", required = false) Long lastUniverseId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UniverseListResponse response = universeService.findBookmarkedUniverses(user.getUsername(), size, lastUniverseId);
        return ResponseData.ok(response, "찜한 유니버스 조회 성공");
    }

    /**
     * 13. 좋아요한 유니버스 목록 조회
     */
    @GetMapping("/me/likes")
    public ResponseEntity<ResponseData<UniverseListResponse>> findLikedUniverses(
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "lastUniverseId", required = false) Long lastUniverseId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UniverseListResponse response = universeService.findLikedUniverses(user.getUsername(), size, lastUniverseId);
        return ResponseData.ok(response, "좋아요한 유니버스 조회 성공");
    }


}