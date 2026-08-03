package com.ebbinghaus.vocab.controller;

import com.ebbinghaus.vocab.config.JwtInterceptor;
import com.ebbinghaus.vocab.domain.vo.Result;
import com.ebbinghaus.vocab.domain.vo.StatisticsOverviewVO;
import com.ebbinghaus.vocab.domain.vo.TrendItemVO;
import com.ebbinghaus.vocab.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "学习统计", description = "总览、趋势、弱项分析")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) { this.statisticsService = statisticsService; }

    @GetMapping("/overview")
    @Operation(summary = "学习总览")
    public Result<StatisticsOverviewVO> overview(HttpServletRequest req) {
        return Result.ok(statisticsService.overview((Long) req.getAttribute(JwtInterceptor.USER_ID_ATTR)));
    }

    @GetMapping("/trend")
    @Operation(summary = "每日趋势")
    public Result<List<TrendItemVO>> trend(@RequestParam(defaultValue = "30") int days, HttpServletRequest req) {
        return Result.ok(statisticsService.trend((Long) req.getAttribute(JwtInterceptor.USER_ID_ATTR), days));
    }

    @GetMapping("/weak-analysis")
    @Operation(summary = "薄弱词分析")
    public Result<List<Map<String,Object>>> weak(HttpServletRequest req) {
        return Result.ok(statisticsService.weakAnalysis((Long) req.getAttribute(JwtInterceptor.USER_ID_ATTR)));
    }
}
