package com.ebbinghaus.vocab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 管理员角色校验拦截器。
 * 在 JwtInterceptor 之后执行，校验 role == ADMIN。
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public AdminAuthInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String role = (String) request.getAttribute(JwtInterceptor.ROLE_ATTR);

        if (!"ADMIN".equals(role)) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            Map<String, Object> body = new java.util.LinkedHashMap<>();
            body.put("code", 403);
            body.put("message", "需要管理员权限");
            body.put("data", null);
            response.getWriter().write(objectMapper.writeValueAsString(body));
            return false;
        }
        return true;
    }
}
