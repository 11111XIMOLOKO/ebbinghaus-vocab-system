package com.ebbinghaus.vocab.config;

import com.ebbinghaus.vocab.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * JWT 认证拦截器。
 * 校验 Authorization header 中的 Bearer token，通过后将用户信息写入 request attribute。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    /** request attribute key：当前登录用户的 ID */
    public static final String USER_ID_ATTR = "userId";
    /** request attribute key：当前登录用户的用户名 */
    public static final String USERNAME_ATTR = "username";
    /** request attribute key：当前登录用户的角色 */
    public static final String ROLE_ATTR = "role";

    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;
    private final ObjectMapper objectMapper;

    public JwtInterceptor(JwtUtil jwtUtil, TokenBlacklist tokenBlacklist, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklist = tokenBlacklist;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 从 Authorization header 提取 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response, "未登录或 token 格式错误");
            return false;
        }

        String token = authHeader.substring(7);

        // 检查黑名单（已登出的 token）
        if (tokenBlacklist.contains(token)) {
            writeUnauthorized(response, "token 已失效，请重新登录");
            return false;
        }

        // 校验 token
        if (!jwtUtil.validateToken(token)) {
            writeUnauthorized(response, "token 无效或已过期");
            return false;
        }

        // 将用户信息写入 request attribute，供 Controller 使用
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        if (userId == null) {
            writeUnauthorized(response, "token 解析失败");
            return false;
        }

        request.setAttribute(USER_ID_ATTR, userId);
        request.setAttribute(USERNAME_ATTR, username);
        request.setAttribute(ROLE_ATTR, role);

        log.debug("JWT authenticated: userId={}, username={}", userId, username);
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("code", 401);
        body.put("message", message);
        body.put("data", null);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
