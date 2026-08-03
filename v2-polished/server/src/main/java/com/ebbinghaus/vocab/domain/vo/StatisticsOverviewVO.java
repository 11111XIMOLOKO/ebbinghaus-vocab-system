package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

@Data
public class StatisticsOverviewVO {
    private Integer totalStudyDays;
    private Integer streakDays;
    private Long totalMastered;
    private Long totalWords;
    private int[] stageDistribution;
    // 参考项目扩展字段
    private Integer learnedWordCount;
    private Integer masteredWordCount;
    private Integer dueReviewCount;
    private Integer wrongWordCount;
    private Integer masteryRate;
    private Integer forgettingRate;
    private Integer reviewCompletionRate;
}
