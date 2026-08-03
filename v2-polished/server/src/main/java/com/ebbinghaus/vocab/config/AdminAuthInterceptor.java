package com.ebbinghaus.vocab.config;

import com.ebbinghaus.vocab.domain.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public AdminAuthInterceptor(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp,
                             Object handler) throws Exception {
        String role = (String) req.getAttribute(JwtInterceptor.ROLE_ATTR);
        if (!"ADMIN".equals(role)) {
            resp.setStatus(403);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(objectMapper.writeValueAsString(Result.error(403, "需要管理员权限")));
            return false;
        }
        return true;
    }
}
