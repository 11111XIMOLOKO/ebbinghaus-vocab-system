package com.ebbinghaus.vocab.controller;

import com.ebbinghaus.vocab.config.JwtInterceptor;
import com.ebbinghaus.vocab.domain.dto.UpdateGoalRequest;
import com.ebbinghaus.vocab.domain.dto.UpdateStudyPlanRequest;
import com.ebbinghaus.vocab.domain.entity.StudyPlan;
import com.ebbinghaus.vocab.domain.vo.*;
import com.ebbinghaus.vocab.service.CheckinService;
import com.ebbinghaus.vocab.service.ScheduleService;
import com.ebbinghaus.vocab.service.StudyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@Tag(name = "复习日程", description = "复习目标、概览、到期查询")
public class ScheduleController {

    private final StudyPlanService studyPlanService;
    private final ScheduleService scheduleService;
    private final CheckinService checkinService;

    public ScheduleController(StudyPlanService studyPlanService, ScheduleService scheduleService,
                              CheckinService checkinService) {
        this.studyPlanService = studyPlanService;
        this.scheduleService = scheduleService;
        this.checkinService = checkinService;
    }

    @GetMapping("/goal")
    @Operation(summary = "获取复习目标")
    public Result<ScheduleGoalVO> getGoal(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        StudyPlan p = studyPlanService.getPlan(userId);
        ScheduleGoalVO vo = new ScheduleGoalVO();
        vo.setPlanWordCount(p.getPlanWordCount()); vo.setReviewMultiplier(p.getReviewMultiplier());
        vo.setDailyReviewCount(p.getDailyReviewCount()); vo.setDailyTotalCount(p.getDailyTotalCount());
        return Result.ok(vo);
    }

    @PutMapping("/goal")
    @Operation(summary = "更新复习倍数")
    public Result<ScheduleGoalVO> updateGoal(@Valid @RequestBody UpdateGoalRequest req,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        UpdateStudyPlanRequest r = new UpdateStudyPlanRequest();
        r.setReviewMultiplier(req.getReviewMultiplier());
        StudyPlan p = studyPlanService.updatePlan(userId, r);
        ScheduleGoalVO vo = new ScheduleGoalVO();
        vo.setPlanWordCount(p.getPlanWordCount()); vo.setReviewMultiplier(p.getReviewMultiplier());
        vo.setDailyReviewCount(p.getDailyReviewCount()); vo.setDailyTotalCount(p.getDailyTotalCount());
        return Result.ok(vo);
    }

    @GetMapping("/overview")
    @Operation(summary = "复习概览（到期词数、阶段分布）")
    public Result<ScheduleOverviewVO> overview(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        return Result.ok(scheduleService.getOverview(userId));
    }

    @GetMapping("/due")
    @Operation(summary = "到期待复习单词列表")
    public Result<List<WordVO>> getDueReviews(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        return Result.ok(scheduleService.getDueReviews(userId));
    }

    @PostMapping("/checkin")
    @Operation(summary = "每日签到")
    public Result<Void> checkin(HttpServletRequest request) {
        checkinService.checkin((Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR));
        return Result.ok();
    }
}
