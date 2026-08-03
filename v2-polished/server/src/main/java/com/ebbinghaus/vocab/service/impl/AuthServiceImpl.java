package com.ebbinghaus.vocab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ebbinghaus.vocab.config.TokenBlacklist;
import com.ebbinghaus.vocab.domain.dto.LoginRequest;
import com.ebbinghaus.vocab.domain.dto.RegisterRequest;
import com.ebbinghaus.vocab.domain.entity.User;
import com.ebbinghaus.vocab.domain.vo.LoginVO;
import com.ebbinghaus.vocab.domain.vo.UserVO;
import com.ebbinghaus.vocab.exception.BusinessException;
import com.ebbinghaus.vocab.mapper.UserMapper;
import com.ebbinghaus.vocab.service.AuthService;
import com.ebbinghaus.vocab.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;
    private final SecretKey secretKey;

    public AuthServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil, TokenBlacklist tokenBlacklist,
                           com.ebbinghaus.vocab.config.JwtProperties jwtProperties) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void register(RegisterRequest request) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (count != null && count > 0) throw new BusinessException("用户名已存在");
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (user == null) throw new BusinessException("用户名或密码错误");
        if (user.getStatus() == null || user.getStatus() == 0)
            throw new BusinessException("账号已被禁用");
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BusinessException("用户名或密码错误");

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginVO vo = new LoginVO();
        vo.setToken(token); vo.setUserId(user.getId());
        vo.setUsername(user.getUsername()); vo.setRole(user.getRole());
        return vo;
    }

    @Override
    public void logout(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
            tokenBlacklist.add(token, claims.getExpiration().getTime());
        } catch (Exception ignored) { /* token already invalid */ }
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        UserVO vo = new UserVO();
        vo.setId(user.getId()); vo.setUsername(user.getUsername());
        vo.setRole(user.getRole()); vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}
