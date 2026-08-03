package com.ebbinghaus.vocab.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ebbinghaus.vocab.config.JwtInterceptor;
import com.ebbinghaus.vocab.domain.entity.*;
import com.ebbinghaus.vocab.domain.vo.Result;
import com.ebbinghaus.vocab.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "后台管理", description = "用户/词库/单词/公告/日志")
public class AdminController {
    private final AdminService svc;

    public AdminController(AdminService svc) { this.svc = svc; }

    // --- 用户 ---
    @GetMapping("/users")
    @Operation(summary = "用户列表")
    public Result<Page<User>> users(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(svc.listUsers(pageNum, pageSize));
    }
    @PutMapping("/users/{id}/toggle")
    @Operation(summary = "启用/禁用")
    public Result<Void> toggle(@PathVariable Long id) { svc.toggleUser(id); return Result.ok(); }
    @DeleteMapping("/users/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> delUser(@PathVariable Long id) { svc.deleteUser(id); return Result.ok(); }

    // --- 词库 ---
    @GetMapping("/word-books")
    public Result<Page<WordBook>> books(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(svc.listWordBooks(pageNum, pageSize));
    }
    @PostMapping("/word-books")
    public Result<WordBook> createBook(@RequestBody Map<String, Object> b) {
        return Result.ok(svc.createWordBook((String) b.get("name"), (String) b.get("description"),
                b.get("sortOrder") != null ? ((Number) b.get("sortOrder")).intValue() : null));
    }
    @PutMapping("/word-books/{id}")
    public Result<Void> updateBook(@PathVariable Long id, @RequestBody Map<String, Object> b) {
        svc.updateWordBook(id, (String) b.get("name"), (String) b.get("description"),
                b.get("sortOrder") != null ? ((Number) b.get("sortOrder")).intValue() : null);
        return Result.ok();
    }
    @DeleteMapping("/word-books/{id}")
    public Result<Void> delBook(@PathVariable Long id) { svc.deleteWordBook(id); return Result.ok(); }

    // --- 单词 ---
    @GetMapping("/words")
    public Result<Page<Word>> words(@RequestParam(required = false) Long bookId,
                                     @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(svc.listWords(bookId, pageNum, pageSize));
    }
    @PostMapping("/words")
    public Result<Word> createWord(@RequestBody Map<String, Object> b) {
        return Result.ok(svc.createWord(((Number) b.get("bookId")).longValue(), (String) b.get("english"), (String) b.get("chinese")));
    }
    @PutMapping("/words/{id}")
    public Result<Void> updateWord(@PathVariable Long id, @RequestBody Map<String, Object> b) {
        svc.updateWord(id, (String) b.get("english"), (String) b.get("chinese")); return Result.ok();
    }
    @DeleteMapping("/words/{id}")
    public Result<Void> delWord(@PathVariable Long id) { svc.deleteWord(id); return Result.ok(); }

    // --- 公告 ---
    @GetMapping("/announcements")
    @Operation(summary = "公告列表")
    public Result<Page<Announcement>> anns(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(svc.listAnnouncements(pageNum, pageSize));
    }
    @PostMapping("/announcements")
    @Operation(summary = "创建公告")
    public Result<Void> createAnn(@RequestBody Map<String, String> b, HttpServletRequest req) {
        svc.createAnnouncement(b.get("title"), b.get("content"), b.get("type"),
                (Long) req.getAttribute(JwtInterceptor.USER_ID_ATTR));
        return Result.ok();
    }
    @PutMapping("/announcements/{id}")
    @Operation(summary = "更新公告")
    public Result<Void> updateAnn(@PathVariable Long id, @RequestBody Map<String, String> b) {
        svc.updateAnnouncement(id, b.get("title"), b.get("content"), b.get("type"),
                b.containsKey("status") ? Integer.valueOf(b.get("status")) : null);
        return Result.ok();
    }
    @DeleteMapping("/announcements/{id}")
    @Operation(summary = "删除公告")
    public Result<Void> delAnn(@PathVariable Long id) { svc.deleteAnnouncement(id); return Result.ok(); }

    // --- 操作日志 ---
    @GetMapping("/operation-logs")
    @Operation(summary = "操作日志")
    public Result<Page<OperationLog>> logs(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(svc.listLogs(pageNum, pageSize));
    }
}
