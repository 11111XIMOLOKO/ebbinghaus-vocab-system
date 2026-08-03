CREATE TABLE IF NOT EXISTS `wrong_word` (
    `id`          BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT  NOT NULL,
    `word_id`     BIGINT  NOT NULL,
    `wrong_count` INT     NOT NULL DEFAULT 1 COMMENT '累计错误次数',
    `status`      INT     NOT NULL DEFAULT 0 COMMENT '0=待复习 1=已掌握',
    `created_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_word` (`user_id`, `word_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错词表';

CREATE TABLE IF NOT EXISTS `checkin_record` (
    `id`               BIGINT NOT NULL AUTO_INCREMENT,
    `user_id`          BIGINT NOT NULL,
    `checkin_date`     DATE   NOT NULL,
    `study_duration`   INT    NOT NULL DEFAULT 0,
    `completed_target` INT    NOT NULL DEFAULT 0 COMMENT '0=未完成 1=已完成',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录表';
