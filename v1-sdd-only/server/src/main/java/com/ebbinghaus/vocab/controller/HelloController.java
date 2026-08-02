package com.ebbinghaus.vocab.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "健康检查", description = "服务健康状态检查")
public class HelloController {

    @Operation(summary = "Hello 测试接口")
    @GetMapping("/api/hello")
    public String hello() {
        return "Ebbinghaus Vocab Server is running!";
    }
}
