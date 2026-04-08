## 1. 프로젝트 개요

부트캠프에서 팀으로 만든 여행 일기 서비스를 혼자 재구현하는 프로젝트다. 기술 스택은 Spring Boot + MyBatis (이후 JPA 마이그레이션 예정)이며, 우선 BE만 구현한다.

학습 목표는 세 가지다. TDD를 체감하는 것, Agile을 유저 스토리 단위로 적용해보는 것, 그리고 생각한 내용을 문서화하는 것이다.

---

## 2. 유저 스토리

### 접두사의 역할

`US-01` 같은 접두사는 유저 스토리 문서의 특정 항목을 가리키는 고유 식별자다. 브랜치명(`feat/US-01`), 커밋 메시지(`feat: US-01 내 프로필 조회`) 등에서
유저 스토리 문서의 어느 항목과 연결되는지 알 수 있게 된다. 평문 설명만으로는 비슷한 기능이 늘어날수록 이름이 겹치거나 모호해질 수 있다.

---

## 3. ERD 설계

### sido / gugun을 테이블로 관리하는 이유

코드값만 저장하고 테이블을 따로 두지 않는 방식도 있다. 그러나 이 프로젝트에서는 여행지 조회 시 `〇〇시 〇〇구` 형태로 지역명을 표시해야 하고, 필터링 옵션도 제공해야 한다. JOIN으로 지역명을 가져오고 정규화된 형태로 관리하기 위해 별도 테이블로 뒀다.

### sido/gugun — surrogate key와 code 분리

sido와 gugun 테이블에서 PK(`id`)와 공공 데이터 코드값(`code`)을 분리했다. 코드값을 PK로 쓰면 코드가 변경될 때 FK를 들고 있는 모든 테이블을 연쇄 업데이트해야 한다. surrogate key(`id`)를 PK로 두면 코드가 바뀌어도 FK 연쇄 업데이트가 없다.

`code`는 조회 키로 쓰이므로 중복을 막아야 한다. sido는 `code`에 UNIQUE 제약, gugun은 sido 내에서 code가 유일해야 하므로 `(sido_id, code)` 복합 UNIQUE 제약을 추가했다.

### attraction.member_id NOT NULL 결정

원래 팀 프로젝트에서는 공공 데이터 import를 고려해 NULL을 허용했다. 공공 데이터는 등록자가 없기 때문이다. 재구현에서도 공공 데이터 import 가능성은 열어두되, 등록자를 ADMIN 계정으로 지정하는 방식으로 변경했다. 덕분에 등록자는 항상 존재하므로 NOT NULL로 변경할 수 있었다.

### bookmark 복합 PK — 관계를 표현하는 테이블

`bookmark`는 회원과 여행지 사이의 관계 자체를 표현하는 테이블이다. 그 관계는 `(attraction_id, member_id)` 조합으로 유일하게 식별되므로 복합 PK가 자연스러운 선택이다. 중복 북마크를 DB 레벨에서 차단하는 것은 부산물이다.

FK가 걸려있어서 회원이나 여행지가 삭제될 때 북마크도 함께 처리되므로, 고아 관계가 남는 문제도 DB 레벨에서 막힌다. 추후 JPA로 마이그레이션할 때는 복합 PK를 `@EmbeddedId`나 `@IdClass`로 표현해야 해서 코드가 번거로워질 수 있다. surrogate key를 두는 것이 JPA에서는 더 편하다는 의견도 있어서, 마이그레이션 시점에 트레이드오프를 직접 경험해볼 예정이다.

### AI 분석 파이프라인 설계

파이프라인은 두 단계로 구성된다.

**Phase 1** — Scheduler가 `is_analyzed = false`인 일기를 조회해 LLM으로 감정을 분류하고, 결과를 `diary_emotion_analysis`에 저장한다. 처리 완료 시 `diary.is_analyzed`를 true로 변경한다.

**Phase 2** — `attraction_emotion_stat`을 읽어 베이지안 스코어를 계산하고, 최고 스코어 감정으로 `attraction.emotion_type_id`를 갱신한 뒤 `ai_report`를 생성한다.

설계 포인트가 몇 가지 있다.

`diary.is_analyzed`에 인덱스를 걸어둔 것은 배치 처리에서도 의미가 있다. `is_analyzed = false`인 행이 전체의 극히 일부일 때, 인덱스 없이 풀스캔하면 이미 처리된 수많은 행을 매번 읽어야 한다. Scheduler 주기가 짧거나 데이터가 많아질수록 차이가 커진다.

`attraction_emotion_stat`은 재계산 비용을 줄이기 위한 누적 통계 테이블이다. 매번 `diary` 전체를 집계하면 비용이 크다. 이 테이블에 감정별 count와 베이지안 스코어를 누적해두면 Phase 2에서 이 테이블만 읽으면 된다.

베이지안 스코어를 쓰는 이유는 단순 count로 대표 감정을 정하면 데이터가 적은 초기에 노이즈에 취약하기 때문이다. 예를 들어 일기가 1개뿐인 여행지에서 그 일기의 감정이 대표 감정이 되는 건 신뢰하기 어렵다. 베이지안 스코어는 사전 확률을 반영해 데이터가 적어도 안정적인 추정이 가능하다.

`diary_emotion_analysis`에 `updated_at`이 없는 것은 의도적이다. AI 분석 로그는 한번 기록되면 수정되지 않는다. 불변 로그임을 스키마 레벨에서 명시한다.

`ai_report.emotion_type_id`는 리포트 생성 당시의 대표 감정 스냅샷이다. 이후 분석이 누적되어 대표 감정이 바뀌어도 당시의 분석 맥락이 보존된다.

### POINT 타입 + SPATIAL INDEX 도입

위경도를 `DECIMAL(20, 17)` 두 컬럼으로 분리하는 대신 `POINT` 타입 단일 컬럼으로 저장했다. 카카오맵 API가 제공하는 대각선 두 점(좌하단·우상단)으로 bounding box를 구성해 `MBRContains`로 범위 조회 시 SPATIAL INDEX가 활용된다.

응답 DTO는 여전히 `latitude`, `longitude`로 분리해서 제공한다. DB 컬럼 타입과 API 응답 필드는 별개이고, 서버에서 `ST_X()`, `ST_Y()`로 분리해 매핑한다.

---

## 4. 패키지 구조 설계

### 도메인 중심 구조 (Domain-first)

패키지를 기술적 역할(controller, service, repository)이 아니라 비즈니스 관심사(member, attraction, diary) 기준으로 묶었다. 기능 하나를 개발할 때 관련 파일이 `domain/attraction/` 안에 모여 있어 탐색 비용이 낮고, 팀 작업 시 도메인별로 담당을 나누면 git 충돌도 줄어든다.

어떤 도메인에도 속하지 않는 공통 관심사는 `global/`로 분리했다. "이게 특정 도메인 로직인가, 아니면 인프라/공통 관심사인가"가 `domain/` vs `global/` 분류 기준이 된다.

### Controller의 도메인 간 의존 문제

팀 프로젝트 `AttractionController`는 `AttractionService`, `BookmarkService`, `RegionService`, `AiService` 네 개의 Service를 주입받고 있었다. 구체적인 문제는 두 가지였다.

첫째, Region ID 변환 로직이 Controller에 있었다.

```java
// AttractionController
Long sidoId = regionService.getSidoIdByCode(request.sidoCode());
Long gugunId = regionService.getGugunIdByCodes(request.sidoCode(), request.gugunCode());
AttractionCreateCommand command = request.toCommand(memberId, sidoId, gugunId);
```

"sidoCode/gugunCode를 ID로 변환해서 attraction을 등록한다"는 비즈니스 흐름인데 Controller에 노출되어 있었다. Controller가 없으면 이 흐름을 재현할 수 없어서, 다른 진입점이 생기면 코드가 중복된다.

둘째, Bookmark 쿼리가 2번 나갔다.

```java
// AttractionController
boolean isBookmarked = bookmarkService.isBookmarked(memberId, attractionId);
int bookmarkCount = bookmarkService.getBookmarkCount(attractionId);
```

`isBookmarked`와 `getBookmarkCount`가 각각 별도 쿼리였다. attraction 상세 조회 쿼리에 bookmark 정보를 JOIN으로 합치면 쿼리 1번으로 해결할 수 있는데, Controller에서 조립하다 보니 이 최적화 여지가 막혀 있었다.

재구현에서는 우선 쿼리 2번을 허용한다. 지금 규모에서 상세 조회 쿼리 2번은 실질적인 성능 문제가 아니고, JOIN으로 합칠 경우 attraction 쿼리가 bookmark를 알게 되어 도메인 경계가 섞인다. 추후 성능 실험을 통해 JOIN 방식과 비교해볼 예정이다.

### 대안 검토

**대안 1 — Facade 패턴**: 여러 Service 조합 로직을 `AttractionFacade`로 추출하고 Controller는 Facade만 참조한다. Controller는 HTTP 관심사만 담당하고, 도메인 간 오케스트레이션은 Facade가 가져간다. 규모가 커질수록 효과적이지만 지금 규모에서는 레이어가 하나 더 생기는 오버헤드가 있다.

**대안 2 — AttractionService가 RegionService를 참조**: Region ID 변환 로직을 `AttractionService` 안으로 흡수하고, 내부에서 `RegionService`를 호출한다. Service가 다른 도메인의 Service를 주입받는 건 단방향이면 일반적으로 허용된다.

**대안 2를 선택한 이유**: 지금 규모에서 Facade는 오버엔지니어링이다. 핵심 문제인 "비즈니스 흐름이 Controller에 노출된 것"을 해결하는 최소한의 방법은 해당 로직을 Service로 내리는 것이다.

### 도메인 선정 기준

도메인을 나누는 핵심 질문은 "이게 독립적인 비즈니스 관심사인가"다. 독립적이라는 건 두 가지로 판단한다.

첫째, 자체 상태를 가지는가. 자기만의 테이블이 있고 생명주기(생성/수정/삭제)가 있으면 도메인이다.

둘째, 다른 것과 분리해서 설명할 수 있는가. "북마크 기능을 설명해봐", "지역 목록 조회 기능을 설명해봐"처럼 독립적인 기능으로 설명할 수 있으면 도메인이다.

이 기준으로 지금 패키지를 보면 `member / attraction / diary / bookmark / region`은 문제없다. `ai`는 `ai_report`, `diary_emotion_analysis`, `attraction_emotion_stat` 같은 AI 전용 테이블이 있고 Scheduler 로직이 복잡하므로 별도 패키지로 격리해두는 게 관리하기 편하다.

### 도메인 패키지와 RESTful API path의 관계

path는 클라이언트가 리소스를 어떻게 인식하느냐의 문제고, 도메인은 서버 내부에서 책임을 어떻게 나누느냐의 문제다. 둘은 참고는 하되 종속되지 않는다.

예를 들어 `GET /api/v1/attraction/{attractionId}/report`는 path가 `attraction` 하위에 있지만 실제로는 `ai` 도메인이 처리한다. "이 리포트는 attraction에 속한 리소스다"라는 클라이언트 관점의 설계와 서버 내부의 책임 분리가 다른 경우다. 반대로 bookmark는 `attraction`의 하위 리소스로 볼 수도 있어서 `POST /api/v1/attraction/{attractionId}/bookmark`도 RESTful하게 맞는 설계지만, 북마크 기능이 독립적으로 충분히 크다고 판단해 별도 path로 분리했다.

### 추가 개선 — RegionRepository 직접 참조

`RegionService`를 보면 비즈니스 로직 없이 Repository를 그대로 위임만 하고 있다.

```java
public Long getSidoIdByCode(int sidoCode) {
    return regionRepository.findSidoIdByCode(sidoCode); // 단순 위임
}
```

이런 경우 Service 레이어는 불필요한 간접층이다. `AttractionService`가 `RegionRepository`를 직접 참조하는 게 더 솔직한 코드다.

원칙적으로 DDD에서 다른 도메인의 Repository를 직접 참조하는 것은 권장하지 않는다. 각 도메인은 자신의 Repository만 가져야 하고, 다른 도메인 데이터가 필요하면 해당 도메인의 Service를 통해 접근하는 게 원칙이다. 그러나 sido/gugun은 공공 데이터 기반의 정적 참조 데이터로, DDD에서 공유 커널(Shared Kernel)로 취급해 여러 도메인이 직접 참조하는 것을 허용하는 경우에 해당한다. 직접 참조를 허용할 수 있을 것 같다.

---

## 5. API 명세서

### 명세 작성 과정에서 발견한 누락

기존 코드와 명세를 대조하는 과정에서 `DiaryCreateRequest`에 10자 이상 검증이 빠져있는 것을 발견했다. 유저 스토리에는 명시된 조건인데 구현에서 누락된 것이다. 테스트가 있었다면 수용 조건을 코드로 옮기는 과정에서 자연스럽게 발견했을 것이다.

### targetDiaryId 네이밍 결정

`GET /api/v1/diary/attraction/{attractionId}`에 `diaryId` 쿼리 파라미터가 있었다. 여행지 상세 페이지에서 특정 일기 카드를 클릭하면 그 일기가 목록 첫 번째로 보이게 하기 위한 파라미터였는데, FE에서 미구현 상태로 남아있었다. 용도가 불명확한 이름이라 `targetDiaryId`로 변경했다.

### 커서 페이징에서 totalCount 제거

`POST /api/v1/attraction/search` 응답이 `SliceResponse`인데 기존 코드는 `countSearchList(command)`를 매번 별도 쿼리로 날리고 있었다. 커서 페이징의 장점인 `COUNT(*)` 회피가 사실상 없는 상태였다.

재구현에서는 `totalCount`를 `SliceResponse`에서 제거하고 `hasNext`만 제공하는 순수 커서 페이징으로 간다. 지도 기반 검색 UI에서 "총 몇 개" 숫자보다 "더 있음/없음"이 더 자연스럽고, 클라이언트가 totalCount를 매 요청에 포함시켜야 하는 부담도 없어진다.

---

## 6. 코드 설계 결정

### ApiResponse — 정적 팩토리 메서드 패턴

`ApiResponse`는 class로 구현하고 생성자를 `@AllArgsConstructor(access = AccessLevel.PRIVATE)`로 숨겼다. `onSuccess`, `onFailure`로만 인스턴스를 만들게 강제해서 `new ApiResponse<>(false, data, null)` 같은 잘못된 조합을 사전에 방지한다.

### PageResponse — record 사용 이유

`PageResponse`는 record로 구현했다. `ApiResponse`와 달리 생성자를 숨길 필요가 없기 때문이다. `of()`는 파생값 계산의 편의를 위한 것이지 잘못된 조합을 방지하는 목적이 아니다. record는 불변 데이터 클래스에 적합하고 생성자/equals/hashCode/toString을 자동으로 생성해 보일러플레이트를 줄인다.

### 정적 팩토리 메서드 네이밍 — of vs from

`of`는 같은 타입이거나 동등한 수준의 값들을 조합해서 인스턴스를 만들 때 쓴다. `from`은 다른 타입에서 변환할 때 쓴다.

`PageResponse.of()`는 `content`, `currentPage`, `size`, `totalElements`를 받아 `PageResponse`를 만드는 값 조합이므로 `of`가 맞다. 반면 엔티티 → DTO 변환처럼 `AttractionResponse.from(attraction)` 형태는 타입 변환이므로 `from`이 맞다.

### 기본 타입 vs 래퍼 타입 (primitive vs wrapper)

기본 타입(`boolean`, `int`, `long` 등)은 null이 될 수 없고, 래퍼 타입(`Boolean`, `Integer`, `Long` 등)은 null이 될 수 있다. FE/클라이언트 관점에서 기본 타입 → 해당 타입, 래퍼 타입 → `해당 타입 | null`로 대응된다.

응답 클래스(`ApiResponse`, `PageResponse`, `SliceResponse`)의 `success`, `hasPrev`, `hasNext`, `currentPage` 등은 반드시 값이 존재해야 하므로 기본 타입으로 통일한다. 래퍼 타입을 쓰면 의도치 않게 "이 필드는 null일 수 있다"는 시그널을 주게 되고 `NullPointerException` 가능성도 생긴다.

엔티티에서는 DB 컬럼 nullable 여부에 따라 결정한다. `NOT NULL` 컬럼이면 기본 타입, nullable 컬럼이면 래퍼 타입을 쓴다.

### SliceResponse를 flat하게 구조 변경

초기 구현에서 `SliceResponse`는 `content`와 `SliceMeta`로 나뉜 중첩 구조였다.

```json
{
  "content": [...],
  "meta": { "hasNext": true, "cursor": { ... } }
}
```

`PageResponse`가 flat한데 `SliceResponse`만 중첩 구조면 클라이언트 입장에서 두 응답 포맷이 다르게 생겨 일관성이 떨어진다. `meta`로 감싸는 이점도 크지 않아서 `SliceMeta`를 제거하고 `hasNext`, `cursor`를 최상위 필드로 올렸다.

```json
{
  "content": [...],
  "hasNext": true,
  "cursor": { ... }
}
```