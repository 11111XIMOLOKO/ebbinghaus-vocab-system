package com.ebbinghaus.vocab.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateGoalRequest {
    @Min(1) @Max(10)
    private Integer reviewMultiplier;
}
