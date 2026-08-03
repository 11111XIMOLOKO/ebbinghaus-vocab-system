CREATE TABLE IF NOT EXISTS `study_plan` (
    `id`                 BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`            BIGINT NOT NULL COMMENT '所属用户',
    `book_id`            BIGINT DEFAULT NULL COMMENT '当前词库',
    `plan_word_count`    INT    NOT NULL DEFAULT 10 COMMENT '每日新词数',
    `review_multiplier`  INT    NOT NULL DEFAULT 1 COMMENT '复习倍数',
    `daily_review_count` INT    NOT NULL DEFAULT 10 COMMENT '每日复习数',
    `daily_total_count`  INT    NOT NULL DEFAULT 20 COMMENT '每日总任务数',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习计划表';

CREATE TABLE IF NOT EXISTS `review_plan` (
    `id`               BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`          BIGINT   NOT NULL COMMENT '所属用户',
    `word_id`          BIGINT   NOT NULL COMMENT '单词',
    `stage`            INT      NOT NULL DEFAULT 0 COMMENT '复习阶段 0-7',
    `status`           INT      NOT NULL DEFAULT 0 COMMENT '0=待复习 1=已完成',
    `familiarity`      INT      DEFAULT NULL COMMENT '1=不认识 2=模糊 3=认识',
    `next_review_time` DATETIME NOT NULL COMMENT '下次复习时间',
    `last_review_time` DATETIME DEFAULT NULL COMMENT '最近复习时间',
    `first_study_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次学习时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_next_review` (`user_id`, `next_review_time`),
    INDEX `idx_user_word` (`user_id`, `word_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='复习计划表';

CREATE TABLE IF NOT EXISTS `study_record` (
    `id`                 BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`            BIGINT NOT NULL COMMENT '所属用户',
    `study_date`         DATE   NOT NULL COMMENT '学习日期',
    `new_word_count`     INT    NOT NULL DEFAULT 0 COMMENT '新学词数',
    `review_word_count`  INT    NOT NULL DEFAULT 0 COMMENT '复习词数',
    `mastered_word_count` INT   NOT NULL DEFAULT 0 COMMENT '掌握词数',
    `wrong_word_count`   INT    NOT NULL DEFAULT 0 COMMENT '错词数',
    `study_duration`     INT    NOT NULL DEFAULT 0 COMMENT '学习时长(分钟)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `study_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';
