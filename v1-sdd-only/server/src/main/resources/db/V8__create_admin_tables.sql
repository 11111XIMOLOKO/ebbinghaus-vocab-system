-- V8: 公告表
CREATE TABLE IF NOT EXISTS `announcement` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`       VARCHAR(255) NOT NULL COMMENT '公告标题',
    `content`     TEXT         NOT NULL COMMENT '公告内容',
    `type`        VARCHAR(50)  NOT NULL DEFAULT 'GENERAL' COMMENT '公告类型',
    `status`      INT          NOT NULL DEFAULT 0 COMMENT '0=草稿 1=已发布',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '发布者 ID',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- V8: 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`        BIGINT       DEFAULT NULL COMMENT '操作者 ID',
    `module`         VARCHAR(100) NOT NULL DEFAULT '' COMMENT '操作模块',
    `operation`      VARCHAR(100) NOT NULL DEFAULT '' COMMENT '操作类型',
    `request_uri`    VARCHAR(255) NOT NULL DEFAULT '' COMMENT '请求路径',
    `request_method` VARCHAR(10)  NOT NULL DEFAULT '' COMMENT 'GET/POST/PUT/DELETE',
    `ip`             VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '操作者 IP',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
