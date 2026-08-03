package com.ebbinghaus.vocab.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitRequest {
    @NotNull(message = "wordId 不能为空")
    private Long wordId;

    @NotNull @Min(1) @Max(3)
    private Integer familiarity; // 1=不认识 2=模糊 3=认识
}
