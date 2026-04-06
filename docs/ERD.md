# ERD 설계 의도

---

## 테이블 목록

| 테이블 | 설명 |
|--------|------|
| `member` | 서비스 사용자를 저장한다. Google OAuth로 가입하며 USER/ADMIN 역할을 가진다. |
| `sido` | 시도 행정구역 데이터를 저장한다. 여행지와 JOIN해 지역명을 표시한다. |
| `gugun` | 구군 행정구역 데이터를 저장한다. sido에 속한다. |
| `content_type` | 여행지 카테고리를 저장한다. |
| `emotion_type` | 감정 종류를 저장한다. AI 파이프라인과 여행지 전반에서 참조된다. |
| `attraction` | 여행지를 저장하는 핵심 테이블이다. 위치, 카테고리, 대표 감정을 가진다. |
| `bookmark` | 회원-여행지 북마크 관계를 저장한다. |
| `diary` | 여행지에 작성하는 일기를 저장한다. AI 분석 대상 여부를 `is_analyzed`로 관리한다. |
| `attraction_emotion_stat` | 여행지별 감정 누적 통계를 저장한다. AI 파이프라인의 재계산 비용을 줄인다. |
| `diary_emotion_analysis` | 일기 감정 분석 결과를 불변 로그로 저장한다. |
| `ai_report` | 여행지에 대한 AI 분석 결과를 저장한다. |

---

## 핵심 설계 결정

### `attraction.emotion_type_id` NULL 허용
여행지가 처음 등록될 때는 대표 감정이 없다. AI 파이프라인이 충분한 일기 데이터를 분석한 후에야 값이 채워진다.

### `bookmark` 복합 PK `(attraction_id, member_id)`
bookmark는 회원과 여행지 사이의 관계 자체를 표현하는 테이블이다. 그 관계는 (attraction_id, member_id) 조합으로 유일하게 식별된다.

### `diary.is_analyzed` DEFAULT 0
Scheduler가 미분석 일기만 골라 처리하기 위한 플래그. 일기 작성 시 0으로 초기화되고, Phase 1 처리 완료 시 1로 변경된다. 재분석을 방지하고 처리 대상을 명확히 한다.

### `attraction_emotion_stat` 누적 통계 테이블
AI Phase 2에서 대표 감정을 갱신할 때마다 `diary` 전체를 재집계하면 비용이 크다. 이 테이블에 감정별 count와 베이지안 스코어를 누적해두면 Scheduler 실행 시 이 테이블만 읽으면 된다.

### `ai_report.emotion_type_id`
리포트 생성 당시의 대표 감정 스냅샷이다. 이후 분석이 누적되어 대표 감정이 바뀌어도 당시의 분석 맥락이 보존된다.

---

## 관계 요약

```
sido 1 ─── N gugun
gugun 1 ─── N attraction
content_type 1 ─── N attraction
emotion_type 1 ─── N attraction
member 1 ─── N attraction
attraction N ─── M member (bookmark)
member 1 ─── N diary
attraction 1 ─── N diary
attraction N ─── M emotion_type (attraction_emotion_stat)
diary 1 ─── N diary_emotion_analysis
emotion_type 1 ─── N diary_emotion_analysis
attraction 1 ─── N ai_report
emotion_type 1 ─── N ai_report
```