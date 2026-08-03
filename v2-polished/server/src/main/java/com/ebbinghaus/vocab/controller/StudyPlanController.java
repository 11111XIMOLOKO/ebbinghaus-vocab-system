package com.ebbinghaus.vocab.controller;

import com.ebbinghaus.vocab.config.JwtInterceptor;
import com.ebbinghaus.vocab.domain.dto.UpdateStudyPlanRequest;
import com.ebbinghaus.vocab.domain.entity.StudyPlan;
import com.ebbinghaus.vocab.domain.vo.Result;
import com.ebbinghaus.vocab.service.StudyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study-plan")
@Tag(name = "学习计划", description = "设置词库、新词数、复习倍数")
public class StudyPlanController {

    private final StudyPlanService studyPlanService;

    public StudyPlanController(StudyPlanService studyPlanService) {
        this.studyPlanService = studyPlanService;
    }

    @GetMapping
    @Operation(summary = "获取学习计划")
    public Result<StudyPlan> getPlan(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        return Result.ok(studyPlanService.getPlan(userId));
    }

    @PutMapping
    @Operation(summary = "更新学习计划")
    public Result<StudyPlan> updatePlan(@Valid @RequestBody UpdateStudyPlanRequest req,
                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        return Result.ok(studyPlanService.updatePlan(userId, req));
    }
}
