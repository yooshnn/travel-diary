# API 명세서

## 공통

### 인증
JWT를 Authorization 헤더에 Bearer 토큰으로 전달한다. 별도 언급이 없으면 인증이 필요하다.

### 응답 포맷
```java
record ApiResponse<T>(
    String status,  // "SUCCESS" | "ERROR"
    T data
)

record PageResponse<T>(
    List<T> content,
    int currentPage,
    int totalPages,
    long totalElements,
    int size,
    boolean hasPrev,
    boolean hasNext
)

record SliceResponse<T, C>(
    List<T> content,
    boolean hasNext,
    int totalCount,
    C cursor
)
```

### 에러
- 400: 필수 입력값 누락 또는 형식 오류
- 401: 토큰 없음 또는 유효하지 않음
- 403: 권한 없음
- 404: 리소스 없음

---

## 회원

### GET /api/v1/member/me
내 프로필을 조회한다.

```java
// Response 200
record MemberInfoResponse(
    Long id,
    String name,
    String role,            // "USER" | "ADMIN"
    String profileImageUrl
)
```

---

### PATCH /api/v1/member/me
내 프로필을 수정한다. `multipart/form-data`

```java
// Request
record MemberUpdateRequest(
    String name,            // 필수
    MultipartFile profileImage  // 선택. 이미지 파일만 허용
)

// Response 200
record MemberInfoResponse(
    Long id,
    String name,
    String role,
    String profileImageUrl
)
```

---

## 여행지

### POST /api/v1/attraction
여행지를 등록한다. `multipart/form-data`

```java
// Request
record AttractionCreateRequest(
    int sidoCode,
    int gugunCode,
    Long contentTypeId,
    String name,
    MultipartFile photo,    // 선택
    String overview,        // 10자 이상
    String address,
    BigDecimal latitude,
    BigDecimal longitude
)

// Response 200
record AttractionCreateResponse(
    Long id
)
```

---

### GET /api/v1/attraction/{attractionId}
여행지 상세 정보를 조회한다.

```java
// Response 200
record AttractionDetailResponse(
    Long memberId,
    Long attractionId,
    Long contentTypeId,
    Long emotionTypeId,     // nullable. AI 분석 전이면 null
    String attractionName,
    String memberName,
    String photo,
    String overview,
    String address,
    BigDecimal latitude,
    BigDecimal longitude,
    boolean isBookmarked,
    int bookmarkCount
)
// Response 404: 존재하지 않는 여행지
```

---

### DELETE /api/v1/attraction/{attractionId}
여행지를 삭제한다. soft delete.

```
Response 200
```

---

### POST /api/v1/attraction/search
지도 범위 내 여행지를 검색·필터링한다.

```java
// Request
record AttractionSearchRequest(
    BigDecimal minLat,          // 필수
    BigDecimal maxLat,          // 필수
    BigDecimal minLng,          // 필수
    BigDecimal maxLng,          // 필수
    List<Long> contentTypeIds,  // 필수. 빈 리스트면 전체
    List<Long> emotionTypeIds,  // 필수. 빈 리스트면 전체
    boolean onlyMine,
    String keyword,             // 필수. 빈 문자열이면 전체
    String sortBy,              // "popular" | "name"
    int size,
    AttractionCursor cursor     // 첫 요청은 null
)

record AttractionCursor(
    Integer bookmarkCount,  // 인기순 정렬 시 사용
    String name,            // 이름순 정렬 시 사용
    Long id                 // 중복 방지용
)

// Response 200
SliceResponse<AttractionSearchItem, AttractionCursor>

record AttractionSearchItem(
    Long attractionId,
    Long contentTypeId,
    Long emotionTypeId,
    String name,
    String photo,
    String address,
    BigDecimal latitude,
    BigDecimal longitude,
    Boolean isBookmarked,
    Integer bookmarkCount,
    Integer diaryCount
)
```

---

### GET /api/v1/attraction/popular
인기 여행지 목록을 조회한다. (북마크 수 기준 상위 10개)

```java
// Response 200
List<AttractionTrendResponse>

record AttractionTrendResponse(
    Long attractionId,
    String attractionName,
    String photo,
    String address,
    long bookmarkCount
)
```

---

### GET /api/v1/attraction/recent
최신 여행지 목록을 조회한다. (등록일 기준 최근 10개)

```java
// Response 200
List<AttractionTrendResponse>

record AttractionTrendResponse(
    Long attractionId,
    String attractionName,
    String photo,
    String address,
    long bookmarkCount
)
```

---

### GET /api/v1/attraction/{attractionId}/report
여행지 AI 분석 리포트를 조회한다.

```java
// Response 200
record AiReportResponse(
    Long emotionTypeId,
    String content,
    LocalDateTime createdAt     // "yyyy-MM-dd HH:mm:ss"
)
// Response 404: 리포트 없음
```

---

## 일기

### POST /api/v1/diary
일기를 작성한다. `multipart/form-data`

```java
// Request
record DiaryCreateRequest(
    Long attractionId,          // 필수
    MultipartFile photo,        // 선택
    String content,             // 필수. 10자 이상 300자 이하
    LocalDate visitDate,        // 필수. 오늘 이전
    boolean isPublic
)

// Response 200
record DiaryCreateResponse(
    Long id
)
```

---

### PATCH /api/v1/diary/{diaryId}
일기를 수정한다. `multipart/form-data`

```java
// Request
record DiaryUpdateRequest(
    Long attractionId,          // 필수
    MultipartFile photo,        // 선택
    String content,             // 필수. 10자 이상 300자 이하
    LocalDate visitDate,        // 필수. 오늘 이전
    boolean isPublic
)

// Response 200
// Response 403: 본인 일기 아님
// Response 404: 일기 없음
```

---

### DELETE /api/v1/diary/{diaryId}
일기를 삭제한다. soft delete.

```
// Response 200
// Response 403: 본인 일기 아님
// Response 404: 일기 없음
```

---

### GET /api/v1/diary/me
내 일기 목록을 날짜별로 조회한다.

```java
// Query Params
LocalDate date   // 필수
Integer page     // 기본값 1
Integer size     // 기본값 10, 최대 50

// Response 200
PageResponse<DiaryMyResponse>

record DiaryMyResponse(
    Long diaryId,
    Long attractionId,
    String attractionName,
    String address,
    String content,
    LocalDate visitDate,
    String photo,
    boolean isPublic
)
```

---

### GET /api/v1/diary/me/calendar
내 일기 활동을 캘린더 형식으로 조회한다.

```java
// Query Params
Integer year    // 필수
Integer month   // 필수. 1~12

// Response 200
List<DiaryCalendarResponse>

record DiaryCalendarResponse(
    LocalDate visitDate,
    int count
)
```

---

### GET /api/v1/diary/attraction/{attractionId}
여행지별 공개 일기 목록을 조회한다.

```java
// Query Params
Long targetDiaryId  // 선택. 특정 일기를 첫 번째로 조회 시 사용
Integer page    // 기본값 1
Integer size    // 기본값 10, 최대 100

// Response 200
PageResponse<DiaryAttractionResponse>

record DiaryAttractionResponse(
    Long diaryId,
    Long memberId,
    String memberName,
    LocalDate visitDate,
    String photo,
    String content
)
```

---

### GET /api/v1/diary/latest
최신 공개 일기 목록을 조회한다. (최근 10개)

```java
// Response 200
List<DiaryLatestResponse>

record DiaryLatestResponse(
    Long diaryId,
    String attractionName,
    String memberName,
    LocalDate visitDate,
    String photo,
    String content
)
```

---

## 북마크

### GET /api/v1/bookmark/attraction/{attractionId}
여행지 북마크 여부를 확인한다.

```java
// Response 200
record BookmarkCheckResponse(
    boolean isBookmarked
)
```

---

### POST /api/v1/bookmark/attraction/{attractionId}
여행지를 북마크한다. 멱등 처리 (이미 북마크된 경우 200 반환).

```
Response 200
```

---

### DELETE /api/v1/bookmark/attraction/{attractionId}
여행지 북마크를 취소한다.

```
Response 200
```

---

### GET /api/v1/bookmark/me
내 북마크 목록을 조회한다.

```java
// Query Params
Integer page    // 기본값 1
Integer size    // 기본값 10, 최대 50

// Response 200
PageResponse<BookmarkedAttractionResponse>

record BookmarkedAttractionResponse(
    Long attractionId,
    String attractionName,
    String photo,
    String address,
    long bookmarkCount
)
```
