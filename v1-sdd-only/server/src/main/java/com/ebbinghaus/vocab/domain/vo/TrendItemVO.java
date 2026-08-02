package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

/**
 * 每日趋势数据点。
 */
@Data
public class TrendItemVO {

    private String date;
    private Integer newWords;
    private Integer reviewWords;
    private Integer masteredWords;
}
