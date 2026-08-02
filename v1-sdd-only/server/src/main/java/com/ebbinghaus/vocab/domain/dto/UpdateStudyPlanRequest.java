package com.ebbinghaus.vocab.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateStudyPlanRequest {

    /** 当前学习的词库 ID */
    private Long bookId;

    /** 每日新词数 */
    @Min(value = 1, message = "每日新词数至少为 1")
    @Max(value = 100, message = "每日新词数最多为 100")
    private Integer planWordCount;

    /** 复习倍数 */
    @Min(1) @Max(10)
    private Integer reviewMultiplier;
}
