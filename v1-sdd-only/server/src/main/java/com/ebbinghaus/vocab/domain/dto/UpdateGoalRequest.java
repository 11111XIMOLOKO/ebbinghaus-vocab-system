package com.ebbinghaus.vocab.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateGoalRequest {

    @Min(value = 1, message = "复习倍数至少为 1")
    @Max(value = 10, message = "复习倍数最多为 10")
    private Integer reviewMultiplier;
}
