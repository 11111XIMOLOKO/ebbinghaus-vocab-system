package com.ebbinghaus.vocab.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ebbinghaus.vocab.domain.entity.Announcement;
import com.ebbinghaus.vocab.domain.vo.Result;
import com.ebbinghaus.vocab.mapper.AnnouncementMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@Tag(name = "公告", description = "用户端公告获取")
public class AnnouncementController {
    private final AnnouncementMapper announcementMapper;

    public AnnouncementController(AnnouncementMapper announcementMapper) { this.announcementMapper = announcementMapper; }

    @GetMapping
    @Operation(summary = "获取已发布公告")
    public Result<Page<Announcement>> list(
            @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize) {
        return Result.ok(announcementMapper.selectPage(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Announcement>().eq(Announcement::getStatus, 1)
                        .orderByDesc(Announcement::getCreateTime)));
    }
}
