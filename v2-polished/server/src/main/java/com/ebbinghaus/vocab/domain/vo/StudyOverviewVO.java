package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

@Data
public class StudyOverviewVO {
    private Integer newWordCount;
    private Integer reviewWordCount;
    private Boolean hasBook;
    private String bookName;
    private Boolean checkedIn;
    private Long totalMastered;
}
