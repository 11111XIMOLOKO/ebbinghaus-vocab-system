package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

@Data
public class WordVO {
    private Long id;
    private String english;
    private String chinese;
    /** 复习阶段 0-7，学习阶段为 null */
    private Integer stage;
    /** 最近熟悉度 1-3，学习阶段为 null */
    private Integer familiarity;
}
