package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

/**
 * 复习目标设置。
 */
@Data
public class ScheduleGoalVO {

    /** 每日新词数 */
    private Integer planWordCount;

    /** 复习倍数 */
    private Integer reviewMultiplier;

    /** 每日复习词数（新词数 × 倍数） */
    private Integer dailyReviewCount;

    /** 每日总任务数 */
    private Integer dailyTotalCount;
}
