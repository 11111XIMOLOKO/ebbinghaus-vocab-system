package com.ebbinghaus.vocab.config;

import com.ebbinghaus.vocab.domain.entity.OperationLog;
import com.ebbinghaus.vocab.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

/**
 * 操作日志拦截器：自动记录 /api/admin/* 的每次请求。
 */
@Component
public class OperationLogInterceptor implements HandlerInterceptor {

    private final OperationLogMapper operationLogMapper;

    public OperationLogInterceptor(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Long userId = (Long) request.getAttribute(JwtInterceptor.USER_ID_ATTR);

        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setModule(extractModule(request.getRequestURI()));
        log.setOperation(request.getMethod());
        log.setRequestUri(request.getRequestURI());
        log.setRequestMethod(request.getMethod());
        log.setIp(getClientIp(request));
        log.setCreateTime(LocalDateTime.now());

        operationLogMapper.insert(log);
    }

    private String extractModule(String uri) {
        if (uri.contains("/admin/users")) return "用户管理";
        if (uri.contains("/admin/word-books")) return "词库管理";
        if (uri.contains("/admin/words")) return "单词管理";
        if (uri.contains("/admin/announcements")) return "公告管理";
        return "后台管理";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip;
    }
}
