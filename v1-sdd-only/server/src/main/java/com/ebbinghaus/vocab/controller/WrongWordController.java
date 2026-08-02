package com.ebbinghaus.vocab.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ebbinghaus.vocab.config.JwtInterceptor;
import com.ebbinghaus.vocab.domain.vo.Result;
import com.ebbinghaus.vocab.service.WrongWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wrong-words")
@Tag(name = "错词本", description = "错词查看、导出、标记掌握")
public class WrongWordController {

    private final WrongWordService wrongWordService;

    public WrongWordController(WrongWordService wrongWordService) {
        this.wrongWordService = wrongWordService;
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取错词列表")
    public Result<Page<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        return Result.ok(wrongWordService.getPage(userId, pageNum, pageSize));
    }

    @GetMapping("/reinforce")
    @Operation(summary = "获取待强化错词")
    public Result<List<Map<String, Object>>> reinforce(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        return Result.ok(wrongWordService.getReinforce(userId));
    }

    @GetMapping("/export")
    @Operation(summary = "导出错词列表")
    public Result<String> export(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        return Result.ok(wrongWordService.export(userId));
    }

    @PostMapping("/{id}/mastered")
    @Operation(summary = "标记错词已掌握")
    public Result<Void> markMastered(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        wrongWordService.markMastered(userId, id);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除错词记录")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        wrongWordService.delete(userId, id);
        return Result.ok();
    }
}
