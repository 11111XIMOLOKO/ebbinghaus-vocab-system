package com.ebbinghaus.vocab.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ebbinghaus.vocab.domain.entity.Announcement;
import com.ebbinghaus.vocab.domain.entity.OperationLog;
import com.ebbinghaus.vocab.domain.entity.User;
import com.ebbinghaus.vocab.domain.entity.Word;
import com.ebbinghaus.vocab.domain.entity.WordBook;
import com.ebbinghaus.vocab.domain.vo.Result;
import com.ebbinghaus.vocab.config.JwtInterceptor;
import com.ebbinghaus.vocab.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "后台管理", description = "用户管理、公告管理、操作日志")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // --- 用户管理 ---
    @GetMapping("/users")
    @Operation(summary = "用户列表")
    public Result<Page<User>> listUsers(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(adminService.listUsers(pageNum, pageSize));
    }

    @PutMapping("/users/{id}/toggle")
    @Operation(summary = "启用/禁用用户")
    public Result<Void> toggleUser(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return Result.ok();
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.ok();
    }

    // --- 公告管理 ---
    @GetMapping("/announcements")
    @Operation(summary = "公告列表")
    public Result<Page<Announcement>> listAnnouncements(@RequestParam(defaultValue = "1") int pageNum,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(adminService.listAnnouncements(pageNum, pageSize));
    }

    @PostMapping("/announcements")
    @Operation(summary = "创建公告")
    public Result<Void> createAnnouncement(@RequestBody Map<String, String> body,
                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);
        adminService.createAnnouncement(
                body.get("title"), body.get("content"), body.get("type"), userId);
        return Result.ok();
    }

    @PutMapping("/announcements/{id}")
    @Operation(summary = "更新公告")
    public Result<Void> updateAnnouncement(@PathVariable Long id,
                                            @RequestBody Map<String, String> body) {
        Integer status = body.containsKey("status") ? Integer.valueOf(body.get("status")) : null;
        adminService.updateAnnouncement(id, body.get("title"), body.get("content"),
                body.get("type"), status);
        return Result.ok();
    }

    @DeleteMapping("/announcements/{id}")
    @Operation(summary = "删除公告")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        adminService.deleteAnnouncement(id);
        return Result.ok();
    }

    // --- 词库管理 ---
    @GetMapping("/word-books")
    @Operation(summary = "词库列表")
    public Result<Page<WordBook>> listWordBooks(@RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(adminService.listWordBooks(pageNum, pageSize));
    }

    @PostMapping("/word-books")
    @Operation(summary = "创建词库")
    public Result<WordBook> createWordBook(@RequestBody Map<String, Object> body) {
        return Result.ok(adminService.createWordBook(
                (String) body.get("name"),
                (String) body.get("description"),
                body.get("sortOrder") != null ? ((Number) body.get("sortOrder")).intValue() : null));
    }

    @PutMapping("/word-books/{id}")
    @Operation(summary = "更新词库")
    public Result<Void> updateWordBook(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        adminService.updateWordBook(id,
                (String) body.get("name"),
                (String) body.get("description"),
                body.get("sortOrder") != null ? ((Number) body.get("sortOrder")).intValue() : null);
        return Result.ok();
    }

    @DeleteMapping("/word-books/{id}")
    @Operation(summary = "删除词库（级联删除词库下所有单词）")
    public Result<Void> deleteWordBook(@PathVariable Long id) {
        adminService.deleteWordBook(id);
        return Result.ok();
    }

    // --- 单词管理 ---
    @GetMapping("/words")
    @Operation(summary = "单词列表（可按词库筛选）")
    public Result<Page<Word>> listWords(@RequestParam(required = false) Long bookId,
                                         @RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(adminService.listWords(bookId, pageNum, pageSize));
    }

    @PostMapping("/words")
    @Operation(summary = "创建单词")
    public Result<Word> createWord(@RequestBody Map<String, Object> body) {
        return Result.ok(adminService.createWord(
                ((Number) body.get("bookId")).longValue(),
                (String) body.get("english"),
                (String) body.get("chinese")));
    }

    @PutMapping("/words/{id}")
    @Operation(summary = "更新单词")
    public Result<Void> updateWord(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        adminService.updateWord(id,
                (String) body.get("english"),
                (String) body.get("chinese"));
        return Result.ok();
    }

    @DeleteMapping("/words/{id}")
    @Operation(summary = "删除单词")
    public Result<Void> deleteWord(@PathVariable Long id) {
        adminService.deleteWord(id);
        return Result.ok();
    }

    // --- 操作日志 ---
    @GetMapping("/operation-logs")
    @Operation(summary = "操作日志列表")
    public Result<Page<OperationLog>> listOperationLogs(@RequestParam(defaultValue = "1") int pageNum,
                                                         @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(adminService.listOperationLogs(pageNum, pageSize));
    }
}
