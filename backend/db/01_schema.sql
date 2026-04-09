-- 1. 시/도
CREATE TABLE `sido` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '시/도 ID',
    `name`       VARCHAR(20)  NOT NULL COMMENT '시/도 이름',
    `code`       INT          NOT NULL COMMENT '시/도 코드',
    `created_at` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sido_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='시도';

-- 2. 구/군
CREATE TABLE `gugun` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '구/군 ID',
    `sido_id`    BIGINT       NOT NULL COMMENT '시/도 ID',
    `name`       VARCHAR(20)  NOT NULL COMMENT '구/군 이름',
    `code`       INT          NOT NULL COMMENT '구/군 코드',
    `created_at` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_gugun_sido_code` (`sido_id`, `code`),
    FOREIGN KEY (`sido_id`) REFERENCES `sido` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='구군';

-- 3. 컨텐츠 종류
CREATE TABLE `content_type` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '컨텐츠 ID',
    `name`       VARCHAR(45)  NOT NULL COMMENT '컨텐츠 이름',
    `created_at` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='컨텐츠 종류';

-- 4. 감정 종류
CREATE TABLE `emotion_type` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '감정 ID',
    `name`       VARCHAR(20)  NOT NULL COMMENT '감정 이름',
    `created_at` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='감정 종류';

-- 5. 멤버
CREATE TABLE `member` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '멤버 ID',
    `name`        VARCHAR(255) NOT NULL COMMENT '이름',
    `provider_id` VARCHAR(100) NOT NULL COMMENT '소셜로그인 ID',
    `provider`    VARCHAR(20)  NOT NULL COMMENT '소셜로그인 종류',
    `role`        ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER' COMMENT '역할',
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0 COMMENT '삭제여부',
    `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='멤버';

-- 6. 여행지 (Attraction)
-- location 컬럼: POINT 타입 + SPATIAL INDEX. 카카오맵 bounding box 범위 조회 시 MBRContains 활용.
-- 응답 DTO에서는 ST_X(location), ST_Y(location)으로 latitude/longitude 분리해 매핑.
-- member_id NOT NULL: 공공 데이터 import 시에도 ADMIN 계정을 등록자로 지정하는 방식으로 처리.
CREATE TABLE `attraction` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '여행지 ID',
    `member_id`       BIGINT       NOT NULL COMMENT '등록자(멤버) ID',
    `sido_id`         BIGINT       NOT NULL COMMENT '시/도 ID',
    `gugun_id`        BIGINT       NOT NULL COMMENT '구/군 ID',
    `content_type_id` BIGINT       NOT NULL COMMENT '컨텐츠 종류 ID',
    `emotion_type_id` BIGINT       NULL COMMENT '대표 감정 ID', -- AI 분석 이전 NULL
    `name`            VARCHAR(100) NOT NULL COMMENT '이름',
    `photo`           VARCHAR(100) NULL COMMENT '사진 URL',
    `location`        POINT        NOT NULL COMMENT '위치 (위도/경도)',
    `address`         VARCHAR(100) NOT NULL COMMENT '상세 주소',
    `overview`        TEXT         NOT NULL COMMENT '소개',
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0 COMMENT '삭제여부',
    `created_at`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`),
    SPATIAL INDEX `idx_attraction_location` (`location`),
    INDEX `idx_attraction_content_type` (`content_type_id`),
    INDEX `idx_attraction_emotion_type` (`emotion_type_id`),
    FOREIGN KEY (`member_id`)       REFERENCES `member`       (`id`),
    FOREIGN KEY (`sido_id`)         REFERENCES `sido`         (`id`),
    FOREIGN KEY (`gugun_id`)        REFERENCES `gugun`        (`id`),
    FOREIGN KEY (`content_type_id`) REFERENCES `content_type` (`id`),
    FOREIGN KEY (`emotion_type_id`) REFERENCES `emotion_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='여행지';

-- 7. 북마크
CREATE TABLE `bookmark` (
    `attraction_id` BIGINT   NOT NULL COMMENT '여행지 ID',
    `member_id`     BIGINT   NOT NULL COMMENT '멤버 ID',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`attraction_id`, `member_id`),
    INDEX `idx_bookmark_member` (`member_id`),
    FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='북마크';

-- 8. 일기
CREATE TABLE `diary` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '일기 ID',
    `member_id`     BIGINT       NOT NULL COMMENT '멤버 ID',
    `attraction_id` BIGINT       NOT NULL COMMENT '여행지 ID',
    `photo`         VARCHAR(100) NULL COMMENT '사진 URL', -- 사진 NULL 허용
    `content`       TEXT         NOT NULL COMMENT '내용',
    `visit_date`    DATE         NOT NULL COMMENT '방문 날짜',
    `is_public`     TINYINT      NOT NULL DEFAULT 1 COMMENT '비/공개 여부',
    `is_analyzed`   TINYINT      NOT NULL DEFAULT 0 COMMENT '감정 분석 여부',
    `is_deleted`    TINYINT      NOT NULL DEFAULT 0 COMMENT '삭제여부',
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`),
    INDEX `idx_diary_member`      (`member_id`),
    INDEX `idx_diary_attraction`  (`attraction_id`),
    INDEX `idx_diary_is_analyzed` (`is_analyzed`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`id`),
    FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='일기';

-- 9. 일기 분석 로그
CREATE TABLE `diary_emotion_analysis` (
    `id`              BIGINT   NOT NULL AUTO_INCREMENT COMMENT '로그 ID',
    `diary_id`        BIGINT   NOT NULL COMMENT '일기 ID',
    `emotion_type_id` BIGINT   NOT NULL COMMENT '감정 ID',
    `created_at`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`diary_id`) REFERENCES `diary` (`id`),
    FOREIGN KEY (`emotion_type_id`) REFERENCES `emotion_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='일기 분석 로그';

-- 10. 여행지별 감정 통계
CREATE TABLE `attraction_emotion_stat` (
    `attraction_id`   BIGINT   NOT NULL COMMENT '여행지 ID',
    `emotion_type_id` BIGINT   NOT NULL COMMENT '감정 ID',
    `count`           INT      NOT NULL DEFAULT 0 COMMENT '감정 언급 횟수',
    `score`           DOUBLE   NOT NULL DEFAULT 0.0 COMMENT '베이지안 스코어',
    `created_at`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`attraction_id`, `emotion_type_id`), -- 복합키 추천
    FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`id`),
    FOREIGN KEY (`emotion_type_id`) REFERENCES `emotion_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='여행지별 감정 통계';

-- 11. AI 리포트
CREATE TABLE `ai_report` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'AI 리포트 ID',
    `attraction_id`   BIGINT       NOT NULL COMMENT '여행지 ID',
    `emotion_type_id` BIGINT       NOT NULL COMMENT '감정 ID',
    `content`         TEXT         NOT NULL COMMENT '내용',
    `created_at`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    `updated_at`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`id`),
    FOREIGN KEY (`emotion_type_id`) REFERENCES `emotion_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 리포트';
