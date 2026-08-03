package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

@Data
public class TrendItemVO {
    private String date;
    private Integer newWords;
    private Integer reviewWords;
    private Integer masteredWords;
    private Integer wrongWords;
    private Integer studyDuration;
}
