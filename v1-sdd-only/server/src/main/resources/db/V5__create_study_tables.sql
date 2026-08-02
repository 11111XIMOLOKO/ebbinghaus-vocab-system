-- V5: 学习计划表（用户可设置每日新词数、复习倍数、当前词库）
CREATE TABLE IF NOT EXISTS `study_plan` (
    `id`                 BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`            BIGINT  NOT NULL COMMENT '所属用户 ID',
    `book_id`            BIGINT  DEFAULT NULL COMMENT '当前学习的词库 ID',
    `plan_word_count`    INT     NOT NULL DEFAULT 10 COMMENT '计划每日学习新词数',
    `review_multiplier`  INT     NOT NULL DEFAULT 1 COMMENT '复习倍数（每日复习数 = 新词数 × 倍数）',
    `daily_review_count` INT     NOT NULL DEFAULT 10 COMMENT '每日复习词数',
    `daily_total_count`  INT     NOT NULL DEFAULT 20 COMMENT '每日总任务数',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习计划表';

-- V5: 复习计划表（核心表——每个词条一条复习记录）
CREATE TABLE IF NOT EXISTS `review_plan` (
    `id`               BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`          BIGINT   NOT NULL COMMENT '所属用户 ID',
    `word_id`          BIGINT   NOT NULL COMMENT '复习的单词 ID',
    `stage`            INT      NOT NULL DEFAULT 0 COMMENT '当前复习阶段（0-7，7=已掌握）',
    `status`           INT      NOT NULL DEFAULT 0 COMMENT '复习状态：0=待复习 1=已完成',
    `familiarity`      INT      DEFAULT NULL COMMENT '最近一次熟悉度：1=不认识 2=模糊 3=认识',
    `next_review_time` DATETIME NOT NULL COMMENT '下次复习时间',
    `last_review_time` DATETIME DEFAULT NULL COMMENT '最近一次复习时间',
    `first_study_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次学习时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_next_review` (`user_id`, `next_review_time`),
    INDEX `idx_user_word` (`user_id`, `word_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='复习计划表';

-- V5: 学习记录表（每日统计汇总）
CREATE TABLE IF NOT EXISTS `study_record` (
    `id`                BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`           BIGINT  NOT NULL COMMENT '所属用户 ID',
    `study_date`        DATE    NOT NULL COMMENT '学习日期',
    `new_word_count`    INT     NOT NULL DEFAULT 0 COMMENT '当日新学词数',
    `review_word_count` INT     NOT NULL DEFAULT 0 COMMENT '当日复习词数',
    `mastered_word_count` INT   NOT NULL DEFAULT 0 COMMENT '当日掌握词数',
    `wrong_word_count`  INT     NOT NULL DEFAULT 0 COMMENT '当日错词数',
    `study_duration`    INT     NOT NULL DEFAULT 0 COMMENT '当日学习时长（分钟）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `study_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';
