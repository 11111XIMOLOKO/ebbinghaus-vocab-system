package com.ebbinghaus.vocab.domain.vo;

import lombok.Data;

/**
 * 登录响应体（T9 登录接口返回）。
 */
@Data
public class LoginVO {

    private String token;
    private Long userId;
    private String username;
    private String role;
}
