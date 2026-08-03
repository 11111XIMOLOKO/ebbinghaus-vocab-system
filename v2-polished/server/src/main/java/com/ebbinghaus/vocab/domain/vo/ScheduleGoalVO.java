package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

@Data
public class ScheduleGoalVO {
    private Integer planWordCount;
    private Integer reviewMultiplier;
    private Integer dailyReviewCount;
    private Integer dailyTotalCount;
}
