package com.ebbinghaus.vocab.controller;

import com.ebbinghaus.vocab.config.JwtInterceptor;
import com.ebbinghaus.vocab.domain.dto.SubmitRequest;
import com.ebbinghaus.vocab.domain.vo.Result;
import com.ebbinghaus.vocab.domain.vo.StudyOverviewVO;
import com.ebbinghaus.vocab.domain.vo.WordVO;
import com.ebbinghaus.vocab.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study")
@Tag(name = "学习流程", description = "学新词、提交结果")
public class StudyController {

    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    @GetMapping("/overview")
    @Operation(summary = "获取学习首页概览")
    public Result<StudyOverviewVO> overview(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        return Result.ok(studyService.getOverview(userId));
    }

    @PostMapping("/new")
    @Operation(summary = "获取待学新词列表（从当前词库随机抽取）")
    public Result<List<WordVO>> getNewWords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        return Result.ok(studyService.getNewWords(userId));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交单词学习/复习结果")
    public Result<Void> submit(@Valid @RequestBody SubmitRequest req,
                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        studyService.submitResult(userId, req.getWordId(), req.getFamiliarity());
        return Result.ok();
    }
}
