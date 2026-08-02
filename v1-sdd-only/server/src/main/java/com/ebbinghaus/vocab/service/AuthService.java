package com.ebbinghaus.vocab.service;

import com.ebbinghaus.vocab.domain.dto.LoginRequest;
import com.ebbinghaus.vocab.domain.dto.RegisterRequest;
import com.ebbinghaus.vocab.domain.vo.LoginVO;
import com.ebbinghaus.vocab.domain.vo.UserVO;

public interface AuthService {

    void register(RegisterRequest request);

    LoginVO login(LoginRequest request);

    /**
     * 用户登出。
     * 将当前 token 加入黑名单，使其不可再用于认证。
     *
     * @param token JWT token 字符串（不含 Bearer 前缀）
     */
    void logout(String token);

    /**
     * 获取当前登录用户信息。
     *
     * @param userId 从 JWT 中解析出的用户 ID
     * @return 用户信息
     * @throws BusinessException 用户不存在时抛出
     */
    UserVO getCurrentUser(Long userId);
}
