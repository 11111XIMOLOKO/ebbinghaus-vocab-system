package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

/**
 * 统计总览。
 */
@Data
public class StatisticsOverviewVO {

    /** 累计学习天数 */
    private Integer totalStudyDays;

    /** 连续打卡天数 */
    private Integer streakDays;

    /** 累计掌握单词数 */
    private Long totalMastered;

    /** 累计学词总数 */
    private Long totalWords;

    /** 各阶段分布：stageDistribution[0]~[7] */
    private int[] stageDistribution;
}
