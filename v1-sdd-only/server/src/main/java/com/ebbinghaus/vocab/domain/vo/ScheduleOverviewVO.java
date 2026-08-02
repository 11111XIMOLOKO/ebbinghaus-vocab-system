package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

/**
 * 复习概览数据。
 */
@Data
public class ScheduleOverviewVO {

    /** 到期待复习词数 */
    private Integer pendingCount;

    /** 复习计划中的总词数 */
    private Integer totalCount;

    /** 各阶段分布：stageDistribution[0]~[7] */
    private int[] stageDistribution;
}
