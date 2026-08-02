package com.ebbinghaus.vocab.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("study_record")
public class StudyRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate studyDate;

    private Integer newWordCount;

    private Integer reviewWordCount;

    private Integer masteredWordCount;

    private Integer wrongWordCount;

    private Integer studyDuration;
}
