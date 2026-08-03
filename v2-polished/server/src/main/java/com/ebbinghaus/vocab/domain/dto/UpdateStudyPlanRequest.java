package com.ebbinghaus.vocab.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateStudyPlanRequest {
    private Long bookId;

    @Min(1) @Max(100)
    private Integer planWordCount;

    @Min(1) @Max(10)
    private Integer reviewMultiplier;
}
