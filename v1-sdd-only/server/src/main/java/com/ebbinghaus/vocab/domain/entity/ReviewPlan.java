package com.ebbinghaus.vocab.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("review_plan")
public class ReviewPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long wordId;

    /** 当前复习阶段 0-7 */
    private Integer stage;

    /** 复习状态：0=待复习 1=已完成 */
    private Integer status;

    /** 最近一次熟悉度：1=不认识 2=模糊 3=认识 */
    private Integer familiarity;

    private LocalDateTime nextReviewTime;

    private LocalDateTime lastReviewTime;

    private LocalDateTime firstStudyTime;
}
