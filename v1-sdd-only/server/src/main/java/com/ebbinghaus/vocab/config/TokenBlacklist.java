package com.ebbinghaus.vocab.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存 Token 黑名单。
 * 用户登出时将 token 加入黑名单，拦截器检查黑名单拒绝请求。
 * 定时清理过期条目（token 本身有 24h 有效期）。
 */
@Component
public class TokenBlacklist {

    /** token → 过期时间戳（毫秒） */
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    /**
     * 将 token 加入黑名单。
     * @param token JWT token
     * @param expiresAt token 的过期时间戳（毫秒）
     */
    public void add(String token, long expiresAt) {
        blacklist.put(token, expiresAt);
    }

    /**
     * 检查 token 是否在黑名单中。
     */
    public boolean contains(String token) {
        return blacklist.containsKey(token);
    }

    /**
     * 每 30 分钟清理已过期的 token。
     */
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void cleanExpired() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}
