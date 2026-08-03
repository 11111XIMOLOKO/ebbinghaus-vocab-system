package com.ebbinghaus.vocab.service;

import com.ebbinghaus.vocab.domain.dto.LoginRequest;
import com.ebbinghaus.vocab.domain.dto.RegisterRequest;
import com.ebbinghaus.vocab.domain.vo.LoginVO;
import com.ebbinghaus.vocab.domain.vo.UserVO;

public interface AuthService {
    void register(RegisterRequest request);
    LoginVO login(LoginRequest request);
    void logout(String token);
    UserVO getCurrentUser(Long userId);
}
