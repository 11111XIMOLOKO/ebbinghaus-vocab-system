package com.ebbinghaus.vocab.config;

import com.ebbinghaus.vocab.domain.entity.OperationLog;
import com.ebbinghaus.vocab.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class OperationLogInterceptor implements HandlerInterceptor {

    private final OperationLogMapper mapper;

    public OperationLogInterceptor(OperationLogMapper mapper) { this.mapper = mapper; }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse resp,
                                Object handler, Exception ex) {
        Long userId = (Long) req.getAttribute(JwtInterceptor.USER_ID_ATTR);
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setModule(module(req.getRequestURI()));
        log.setOperation(req.getMethod());
        log.setRequestUri(req.getRequestURI());
        log.setRequestMethod(req.getMethod());
        log.setIp(req.getRemoteAddr());
        log.setCreateTime(LocalDateTime.now());
        mapper.insert(log);
    }

    private String module(String uri) {
        if (uri.contains("/admin/users")) return "用户管理";
        if (uri.contains("/admin/word-books")) return "词库管理";
        if (uri.contains("/admin/words")) return "单词管理";
        if (uri.contains("/admin/announcements")) return "公告管理";
        return "后台管理";
    }
}
