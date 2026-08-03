package com.ebbinghaus.vocab.config;

import com.ebbinghaus.vocab.domain.vo.Result;
import com.ebbinghaus.vocab.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    public static final String USER_ID_ATTR = "userId";
    public static final String USERNAME_ATTR = "username";
    public static final String ROLE_ATTR = "role";

    private final JwtUtil jwtUtil;
    private final TokenBlacklist blacklist;
    private final ObjectMapper objectMapper;

    public JwtInterceptor(JwtUtil jwtUtil, TokenBlacklist blacklist, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.blacklist = blacklist;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp,
                             Object handler) throws Exception {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeError(resp, 401, "未登录或 token 格式错误");
            return false;
        }
        String token = header.substring(7);

        if (blacklist.contains(token)) {
            writeError(resp, 401, "token 已失效，请重新登录");
            return false;
        }

        if (!jwtUtil.validateToken(token)) {
            writeError(resp, 401, "token 无效或已过期");
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            writeError(resp, 401, "token 解析失败");
            return false;
        }

        req.setAttribute(USER_ID_ATTR, userId);
        req.setAttribute(USERNAME_ATTR, jwtUtil.getUsernameFromToken(token));
        req.setAttribute(ROLE_ATTR, jwtUtil.getRoleFromToken(token));
        return true;
    }

    private void writeError(HttpServletResponse resp, int code, String msg) throws Exception {
        resp.setStatus(code);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(objectMapper.writeValueAsString(Result.error(code, msg)));
    }
}
