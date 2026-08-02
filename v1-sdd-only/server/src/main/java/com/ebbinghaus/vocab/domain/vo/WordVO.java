package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

/**
 * 单词展示对象（含英文 + 中文释义）。
 */
@Data
public class WordVO {

    private Long id;

    private String english;

    private String chinese;
}
