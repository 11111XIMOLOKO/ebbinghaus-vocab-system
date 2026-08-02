package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

/**
 * 学习首页概览数据。
 */
@Data
public class StudyOverviewVO {

    /** 当日待学新词数 */
    private Integer newWordCount;

    /** 当日待复习词数 */
    private Integer reviewWordCount;

    /** 是否已选词库 */
    private Boolean hasBook;

    /** 当前词库名称 */
    private String bookName;

    /** 今日是否已签到 */
    private Boolean checkedIn;

    /** 累计掌握单词数 */
    private Long totalMastered;
}
