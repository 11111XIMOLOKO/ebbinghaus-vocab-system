package com.ebbinghaus.vocab.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public void add(String token, long expiresAt) { blacklist.put(token, expiresAt); }

    public boolean contains(String token) { return blacklist.containsKey(token); }

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void cleanExpired() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(e -> e.getValue() < now);
    }
}
