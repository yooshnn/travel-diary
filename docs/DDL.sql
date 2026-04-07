CREATE TABLE `member` (
    `id`          BIGINT          NOT NULL,
    `name`        VARCHAR(255)    NOT NULL,
    `provider_id` VARCHAR(100)    NOT NULL,
    `provider`    VARCHAR(20)     NOT NULL,
    `role`        ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    `is_deleted`  TINYINT         NOT NULL DEFAULT 0,
    `created_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `sido` (
    `id`         BIGINT      NOT NULL,
    `name`       VARCHAR(20) NULL DEFAULT NULL,
    `code`       INT         NOT NULL,
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sido_code` (`code`)
);

CREATE TABLE `gugun` (
    `id`         BIGINT      NOT NULL,
    `sido_id`    BIGINT      NOT NULL,
    `name`       VARCHAR(20) NULL DEFAULT NULL,
    `code`       INT         NOT NULL,
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_gugun_sido_code` (`sido_id`, `code`),
    FOREIGN KEY (`sido_id`) REFERENCES `sido` (`id`)
);

CREATE TABLE `content_type` (
    `id`         BIGINT       NOT NULL,
    `name`       VARCHAR(45)  NULL DEFAULT NULL,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `emotion_type` (
    `id`         BIGINT      NOT NULL,
    `name`       VARCHAR(20) NULL DEFAULT NULL,
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE `attraction` (
    `id`              BIGINT          NOT NULL,
    `member_id`       BIGINT          NOT NULL,
    `sido_id`         BIGINT          NOT NULL,
    `gugun_id`        BIGINT          NOT NULL,
    `content_type_id` BIGINT          NOT NULL,
    `emotion_type_id` BIGINT          NULL,
    `name`            VARCHAR(100)    NULL DEFAULT NULL,
    `photo`           VARCHAR(100)    NULL DEFAULT NULL,
    `location`        POINT           NOT NULL,
    `address`         VARCHAR(100)    NULL DEFAULT NULL,
    `overview`        TEXT            NULL DEFAULT NULL,
    `is_deleted`      TINYINT         NOT NULL DEFAULT 0,
    `created_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    SPATIAL INDEX `idx_attraction_location` (`location`),
    INDEX `idx_attraction_content_type` (`content_type_id`),
    INDEX `idx_attraction_emotion_type` (`emotion_type_id`),
    FOREIGN KEY (`member_id`)       REFERENCES `member`       (`id`),
    FOREIGN KEY (`sido_id`)         REFERENCES `sido`         (`id`),
    FOREIGN KEY (`gugun_id`)        REFERENCES `gugun`        (`id`),
    FOREIGN KEY (`content_type_id`) REFERENCES `content_type` (`id`),
    FOREIGN KEY (`emotion_type_id`) REFERENCES `emotion_type` (`id`)
);

CREATE TABLE `bookmark` (
    `attraction_id` BIGINT   NOT NULL,
    `member_id`     BIGINT   NOT NULL,
    `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`attraction_id`, `member_id`),
    INDEX `idx_bookmark_member` (`member_id`),
    FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`id`),
    FOREIGN KEY (`member_id`)     REFERENCES `member`     (`id`)
);

CREATE TABLE `diary` (
    `id`            BIGINT       NOT NULL,
    `member_id`     BIGINT       NOT NULL,
    `attraction_id` BIGINT       NOT NULL,
    `photo`         VARCHAR(100) NULL DEFAULT NULL,
    `content`       TEXT         NULL DEFAULT NULL,
    `visit_date`    DATE         NULL DEFAULT NULL,
    `is_public`     TINYINT      NOT NULL DEFAULT 1,
    `is_analyzed`   TINYINT      NOT NULL DEFAULT 0,
    `is_deleted`    TINYINT      NOT NULL DEFAULT 0,
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_diary_member`      (`member_id`),
    INDEX `idx_diary_attraction`  (`attraction_id`),
    INDEX `idx_diary_is_analyzed` (`is_analyzed`),
    FOREIGN KEY (`member_id`)     REFERENCES `member`     (`id`),
    FOREIGN KEY (`attraction_id`) REFERENCES `attraction` (`id`)
);

CREATE TABLE `attraction_emotion_stat` (
    `attraction_id`   BIGINT   NOT NULL,
    `emotion_type_id` BIGINT   NOT NULL,
    `count`           INT      NOT NULL DEFAULT 0,
    `score`           DOUBLE   NOT NULL DEFAULT 0,
    `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`attraction_id`, `emotion_type_id`),
    FOREIGN KEY (`attraction_id`)   REFERENCES `attraction`  (`id`),
    FOREIGN KEY (`emotion_type_id`) REFERENCES `emotion_type` (`id`)
);

CREATE TABLE `diary_emotion_analysis` (
    `id`              BIGINT   NOT NULL,
    `diary_id`        BIGINT   NOT NULL,
    `emotion_type_id` BIGINT   NOT NULL,
    `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`diary_id`)        REFERENCES `diary`        (`id`),
    FOREIGN KEY (`emotion_type_id`) REFERENCES `emotion_type` (`id`)
);

CREATE TABLE `ai_report` (
    `id`              BIGINT   NOT NULL,
    `attraction_id`   BIGINT   NOT NULL,
    `emotion_type_id` BIGINT   NOT NULL,
    `content`         TEXT     NULL DEFAULT NULL,
    `created_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`attraction_id`)   REFERENCES `attraction`  (`id`),
    FOREIGN KEY (`emotion_type_id`) REFERENCES `emotion_type` (`id`)
);
