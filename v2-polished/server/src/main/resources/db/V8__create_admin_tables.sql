CREATE TABLE IF NOT EXISTS `announcement` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `title`       VARCHAR(255) NOT NULL,
    `content`     TEXT         NOT NULL,
    `type`        VARCHAR(50)  NOT NULL DEFAULT 'GENERAL',
    `status`      INT          NOT NULL DEFAULT 0 COMMENT '0=草稿 1=已发布',
    `create_by`   BIGINT       DEFAULT NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

CREATE TABLE IF NOT EXISTS `operation_log` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`        BIGINT       DEFAULT NULL,
    `module`         VARCHAR(100) NOT NULL DEFAULT '',
    `operation`      VARCHAR(100) NOT NULL DEFAULT '',
    `request_uri`    VARCHAR(255) NOT NULL DEFAULT '',
    `request_method` VARCHAR(10)  NOT NULL DEFAULT '',
    `ip`             VARCHAR(50)  NOT NULL DEFAULT '',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
