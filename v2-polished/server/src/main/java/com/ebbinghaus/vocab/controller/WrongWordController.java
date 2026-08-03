package com.ebbinghaus.vocab.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ebbinghaus.vocab.config.JwtInterceptor;
import com.ebbinghaus.vocab.domain.vo.Result;
import com.ebbinghaus.vocab.service.WrongWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wrong-words")
@Tag(name = "错词本", description = "错词查看与操作")
public class WrongWordController {
    private final WrongWordService wrongWordService;

    public WrongWordController(WrongWordService wrongWordService) { this.wrongWordService = wrongWordService; }

    @GetMapping("/page")
    @Operation(summary = "分页获取错词")
    public Result<Page<Map<String,Object>>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute(JwtInterceptor.USER_ID_ATTR);
        return Result.ok(wrongWordService.page(userId, pageNum, pageSize));
    }

    @PostMapping("/{id}/mastered")
    @Operation(summary = "标记掌握")
    public Result<Void> master(@PathVariable Long id, HttpServletRequest req) {
        wrongWordService.markMastered((Long) req.getAttribute(JwtInterceptor.USER_ID_ATTR), id);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除错词")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest req) {
        wrongWordService.delete((Long) req.getAttribute(JwtInterceptor.USER_ID_ATTR), id);
        return Result.ok();
    }
}
