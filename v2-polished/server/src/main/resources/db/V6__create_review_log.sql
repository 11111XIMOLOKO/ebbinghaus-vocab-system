CREATE TABLE IF NOT EXISTS `review_log` (
    `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`      BIGINT      NOT NULL COMMENT '用户',
    `word_id`      BIGINT      NOT NULL COMMENT '单词',
    `stage_before` INT         NOT NULL COMMENT '复习前轮次',
    `result`       VARCHAR(20) NOT NULL COMMENT 'KNOWN / FUZZY / UNKNOWN',
    `reviewed_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '复习时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_reviewed` (`user_id`, `reviewed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='复习记录表';
