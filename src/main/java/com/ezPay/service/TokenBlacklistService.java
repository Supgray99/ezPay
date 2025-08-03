package com.ezPay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlacklistService {

    private static final String PREFIX = "blacklisted:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void blacklistToken(String jti, long expirationSeconds) {
        redisTemplate.opsForValue().set(PREFIX + jti, "true", Duration.ofSeconds(expirationSeconds));
    }

    public boolean isTokenBlacklisted(String jti) {
        Boolean exists = redisTemplate.hasKey(PREFIX + jti);
        return exists != null && exists;
    }
}
