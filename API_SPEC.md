# RePlay API 명세서

프론트엔드 연동을 위한 API 문서

---

## 공통 응답 형식

```json
{
  "success": true,
  "message": "성공 메시지",
  "data": { ... }
}
```

**에러 응답:**
```json
{
  "success": false,
  "message": "에러 메시지",
  "data": null
}
```

---

## 인증

- 로그인 필요 API는 Header에 JWT 토큰 필요
- `Authorization: Bearer {token}`

---

# Universe API

Base URL: `/api/universes`

## 1. 목록 조회 (무한스크롤)

```
GET /api/universes
```

### Query Parameters

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| size | int | X | 10 | 페이지 크기 |
| sort | string | X | latest | 정렬 방식 |
| lastUniverseId | long | X | null | 커서 (마지막 ID) |
| lastLikeCount | long | X | null | 인기순 정렬 시 커서 |

### 정렬 옵션 (sort)

| 값 | 설명 |
|----|------|
| latest | 최신순 (기본값) |
| popular | 인기순 (좋아요 수) |

### Response

```json
{
  "success": true,
  "message": "전체 조회 성공",
  "data": {
    "content": [
      {
        "universeId": 1,
        "title": "나만의 플레이리스트",
        "thumbnailUrl": "https://s3.../thumbnail.jpg",
        "nickName": "음악러버",
        "like": 150
      },
      {
        "universeId": 2,
        "title": "감성 발라드 모음",
        "thumbnailUrl": "https://s3.../thumbnail2.jpg",
        "nickName": "발라드킹",
        "like": 89
      }
    ],
    "pagination": {
      "hasNext": true,
      "lastUniverseId": 2,
      "lastLikeCount": 89,
      "size": 10
    }
  }
}
```

### 프론트엔드 사용 예시

```javascript
// 첫 페이지
const res = await fetch('/api/universes?size=10&sort=latest');

// 다음 페이지 (무한스크롤)
const res = await fetch(`/api/universes?size=10&sort=latest&lastUniverseId=${lastId}`);

// 인기순
const res = await fetch(`/api/universes?size=10&sort=popular&lastUniverseId=${lastId}&lastLikeCount=${lastLike}`);
```

---

## 2. 키워드 검색

```
GET /api/universes/search
```

### Query Parameters

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| keyword | string | O | - | 검색어 (최소 2글자) |
| condition | string | X | all | 검색 조건 |
| size | int | X | 10 | 페이지 크기 |
| sort | string | X | latest | 정렬 방식 |
| lastUniverseId | long | X | null | 커서 |
| lastLikeCount | long | X | null | 인기순 커서 |

### 검색 조건 (condition)

| 값 | 설명 |
|----|------|
| all | 전체 (제목 + 닉네임) |
| title | 제목만 |
| nickname | 닉네임만 |
| memberId | 회원 ID |
| hashtag | 해시태그 |

### Response

목록 조회와 동일한 형식

### 프론트엔드 사용 예시

```javascript
// 전체 검색
const res = await fetch('/api/universes/search?keyword=발라드&condition=all');

// 제목 검색
const res = await fetch('/api/universes/search?keyword=플레이리스트&condition=title');

// 해시태그 검색
const res = await fetch('/api/universes/search?keyword=힙합&condition=hashtag');
```

---

## 3. 상세 조회

```
GET /api/universes/{universeId}
```

### Path Parameters

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| universeId | long | 유니버스 ID |

### Response

```json
{
  "success": true,
  "message": "상세 조회 성공",
  "data": {
    "universeId": 1,
    "title": "나만의 플레이리스트",
    "layoutData": "{\"blocks\":[...],\"theme\":\"dark\"}",
    "themeCode": "THEME_DARK",
    "thumbnailUrl": "https://s3.../thumbnail.jpg",
    "memberId": "user123",
    "nickName": "음악러버",
    "like": 150,
    "bookmark": 45
  }
}
```

### layoutData 구조 (JSON String)

```json
{
  "blocks": [
    {
      "type": "playlist",
      "playlistId": 123,
      "position": { "x": 0, "y": 0 }
    },
    {
      "type": "text",
      "content": "내가 좋아하는 노래들",
      "position": { "x": 0, "y": 1 }
    }
  ],
  "theme": "dark"
}
```

---

## 4. 생성

```
POST /api/universes
Content-Type: multipart/form-data
Authorization: Bearer {token}
```

### Request Body (multipart/form-data)

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| request | JSON | O | 유니버스 정보 |
| file | File | X | 썸네일 이미지 |

### request JSON 구조

```json
{
  "title": "나만의 플레이리스트",
  "layoutData": "{\"blocks\":[...]}",
  "themeCode": "THEME_DARK"
}
```

### Response

```json
{
  "success": true,
  "message": "유니버스 생성 성공",
  "data": null
}
```

### 프론트엔드 사용 예시

```javascript
const formData = new FormData();
formData.append('request', new Blob([JSON.stringify({
  title: '나만의 플레이리스트',
  layoutData: JSON.stringify({ blocks: [...] }),
  themeCode: 'THEME_DARK'
})], { type: 'application/json' }));
formData.append('file', thumbnailFile);

const res = await fetch('/api/universes', {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${token}` },
  body: formData
});
```

---

## 5. 수정

```
PATCH /api/universes/{universeId}
Content-Type: application/json
Authorization: Bearer {token}
```

### Request Body

```json
{
  "title": "수정된 제목",
  "layoutData": "{\"blocks\":[...]}",
  "themeCode": "THEME_LIGHT",
  "status": "Y"
}
```

### Response

```json
{
  "success": true,
  "message": "유니버스 수정 성공",
  "data": {
    "universeId": 1,
    "title": "수정된 제목",
    ...
  }
}
```

---

## 6. 삭제

```
DELETE /api/universes/{universeId}
Authorization: Bearer {token}
```

### Response

```json
{
  "success": true,
  "message": "유니버스 삭제 성공",
  "data": {
    "universeId": 1,
    "title": "삭제된 유니버스",
    ...
  }
}
```

---

## 7. 좋아요

```
POST /api/universes/{universeId}/like
Authorization: Bearer {token}
```

### Response

```json
{
  "success": true,
  "message": "좋아요를 눌렀습니다.",
  "data": {
    "universeId": 1,
    "liked": true,
    "likeCount": 151
  }
}
```

---

## 8. 좋아요 취소

```
DELETE /api/universes/{universeId}/like
Authorization: Bearer {token}
```

### Response

```json
{
  "success": true,
  "message": "좋아요를 취소했습니다.",
  "data": {
    "universeId": 1,
    "liked": false,
    "likeCount": 150
  }
}
```

---

## 9. 북마크 (찜하기)

```
POST /api/universes/{universeId}/bookmark
Authorization: Bearer {token}
```

### Response

```json
{
  "success": true,
  "message": "찜하기를 눌렀습니다.",
  "data": {
    "universeId": 1,
    "bookmarked": true,
    "bookmarkCount": 46
  }
}
```

---

## 10. 북마크 취소

```
DELETE /api/universes/{universeId}/bookmark
Authorization: Bearer {token}
```

### Response

```json
{
  "success": true,
  "message": "찜하기를 취소했습니다.",
  "data": {
    "universeId": 1,
    "bookmarked": false,
    "bookmarkCount": 45
  }
}
```

---

## 11. 신고

```
POST /api/universes/{universeId}/report
Content-Type: application/json
Authorization: Bearer {token}
```

### Request Body

```json
{
  "reasonCode": "SPAM",
  "description": "광고성 게시물입니다."
}
```

### 신고 사유 코드 (reasonCode)

| 코드 | 설명 |
|------|------|
| SPAM | 스팸/광고 |
| INAPPROPRIATE | 부적절한 콘텐츠 |
| COPYRIGHT | 저작권 침해 |
| OTHER | 기타 |

### Response

```json
{
  "success": true,
  "message": "신고가 접수되었습니다.",
  "data": {
    "reportId": 123,
    "targetType": "UNIVERSE",
    "targetId": 1
  }
}
```

---

# Shortform API

Base URL: `/api/shortforms`

> Universe API와 동일한 구조, **북마크 기능 제외**

## 엔드포인트 목록

| # | Method | Endpoint | 기능 | 인증 |
|---|--------|----------|------|------|
| 1 | GET | `/api/shortforms` | 목록 조회 | X |
| 2 | GET | `/api/shortforms/search` | 검색 | X |
| 3 | GET | `/api/shortforms/{id}` | 상세 조회 | X |
| 4 | POST | `/api/shortforms` | 생성 | O |
| 5 | PATCH | `/api/shortforms/{id}` | 수정 | O |
| 6 | DELETE | `/api/shortforms/{id}` | 삭제 | O |
| 7 | POST | `/api/shortforms/{id}/like` | 좋아요 | O |
| 8 | DELETE | `/api/shortforms/{id}/like` | 좋아요 취소 | O |
| 9 | POST | `/api/shortforms/{id}/report` | 신고 | O |

---

## Shortform 응답 필드

### 목록 조회 응답

```json
{
  "success": true,
  "message": "전체 조회 성공",
  "data": {
    "content": [
      {
        "shortFormId": 1,
        "shortFormTitle": "오늘의 추천곡",
        "videoUrl": "https://s3.../video.mp4",
        "thumbnailUrl": "https://s3.../thumb.jpg",
        "caption": "이 노래 진짜 좋아요!",
        "duration": 30,
        "nickName": "음악러버",
        "like": 234
      }
    ],
    "pagination": {
      "hasNext": true,
      "lastShortFormId": 1,
      "lastLikeCount": 234,
      "size": 10
    }
  }
}
```

### 상세 조회 응답

```json
{
  "success": true,
  "message": "상세 조회 성공",
  "data": {
    "shortFormId": 1,
    "shortFormTitle": "오늘의 추천곡",
    "videoUrl": "https://s3.../video.mp4",
    "thumbnailUrl": "https://s3.../thumb.jpg",
    "caption": "이 노래 진짜 좋아요!",
    "duration": 30,
    "memberId": "user123",
    "nickName": "음악러버",
    "like": 234,
    "createdAt": "2025-01-20T14:30:00"
  }
}
```

### 생성 요청

```
POST /api/shortforms
Content-Type: multipart/form-data
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| request | JSON | O | {shortFormTitle, caption} |
| video | File | O | 동영상 파일 |
| thumbnail | File | X | 썸네일 (없으면 자동 생성) |

```json
{
  "shortFormTitle": "오늘의 추천곡",
  "caption": "이 노래 진짜 좋아요!"
}
```

---

# 카멜케이스 수정 필요 사항

## UniverseDTO.java

| 현재 | 수정 |
|------|------|
| `NickName` | `nickName` |
| `createAt` | `createDate` |

## ShortFormDTO.java (생성 시)

모든 필드 카멜케이스 준수:
- `shortFormId`
- `shortFormTitle`
- `videoUrl`
- `thumbnailUrl`
- `caption`
- `duration`
- `memberId`
- `nickName`
- `like`
- `createdAt`

---

# 에러 코드

| HTTP Status | 설명 |
|-------------|------|
| 400 | 잘못된 요청 (파라미터 오류) |
| 401 | 인증 필요 (토큰 없음/만료) |
| 403 | 권한 없음 (본인 아님) |
| 404 | 리소스 없음 |
| 500 | 서버 오류 |

### 에러 응답 예시

```json
{
  "success": false,
  "message": "해당 유니버스를 찾을 수 없습니다.",
  "data": null
}
```

```json
{
  "success": false,
  "message": "해당 유니버스에 대한 권한이 없습니다.",
  "data": null
}
```

---

# 변경 이력

| 날짜 | 버전 | 내용 |
|------|------|------|
| 2025-01-23 | 1.0 | 최초 작성 |
