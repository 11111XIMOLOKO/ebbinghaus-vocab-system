package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

@Data
public class ScheduleOverviewVO {
    private Integer pendingCount;
    private Integer totalCount;
    private int[] stageDistribution;
}
