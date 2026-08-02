package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 当前用户信息响应体。
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String role;
    private Integer status;
    private LocalDateTime createdAt;
}
