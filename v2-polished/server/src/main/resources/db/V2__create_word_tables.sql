CREATE TABLE IF NOT EXISTS `word_book` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(255) NOT NULL COMMENT '词库名称',
    `description` VARCHAR(500) DEFAULT '' COMMENT '词库描述',
    `word_count`  INT          NOT NULL DEFAULT 0 COMMENT '单词数量',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序顺序',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='词库表';

CREATE TABLE IF NOT EXISTS `word` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `word_book_id` BIGINT       NOT NULL COMMENT '所属词库 ID',
    `english`      VARCHAR(255) NOT NULL COMMENT '英文单词',
    `chinese`      VARCHAR(255) NOT NULL COMMENT '中文释义',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_word_book_id` (`word_book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单词表';
