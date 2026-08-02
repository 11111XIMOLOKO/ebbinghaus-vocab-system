package com.ebbinghaus.vocab.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitRequest {

    @NotNull(message = "wordId 不能为空")
    private Long wordId;

    /** 1=不认识 2=模糊 3=认识。学习阶段只允许 1 或 3 */
    @NotNull(message = "familiarity 不能为空")
    @Min(1) @Max(3)
    private Integer familiarity;
}
