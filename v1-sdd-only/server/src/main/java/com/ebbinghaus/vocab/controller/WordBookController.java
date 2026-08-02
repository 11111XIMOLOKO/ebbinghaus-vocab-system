package com.ebbinghaus.vocab.controller;

import com.ebbinghaus.vocab.domain.entity.WordBook;
import com.ebbinghaus.vocab.domain.vo.Result;
import com.ebbinghaus.vocab.service.WordBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/word-books")
@Tag(name = "词库管理", description = "词库浏览与选择")
public class WordBookController {

    private final WordBookService wordBookService;

    public WordBookController(WordBookService wordBookService) {
        this.wordBookService = wordBookService;
    }

    @GetMapping
    @Operation(summary = "获取全部词库列表")
    public Result<List<WordBook>> list() {
        return Result.ok(wordBookService.listAll());
    }
}
