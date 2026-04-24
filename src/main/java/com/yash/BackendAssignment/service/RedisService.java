package com.yash.BackendAssignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String key(Long postId) {
        return "post:" + postId + ":score";
    }

    public void incrementScore(Long postId, int inc) {
        redisTemplate.opsForValue().increment(key(postId), inc);
    }

    public Long getScore(Long postId) {
        Object val = redisTemplate.opsForValue().get(key(postId));
        return val == null ? 0L : Long.valueOf(val.toString());
    }

    public boolean allowBot(Long postId) {
        String key = "post:" + postId + ":bot_count";
        Long count = redisTemplate.opsForValue().increment(key);
        return count <= 100;
    }

    public boolean allowBotToUser(Long botId, Long userId) {
        String key = "cooldown:bot_" + botId + ":user_" + userId;

        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            return false;
        }

        redisTemplate.opsForValue().set(key, "1", 600, TimeUnit.SECONDS);
        return true;
    }

    public void handleNotification(Long userId, String message) {

        String cooldownKey = "notif:cooldown:user_" + userId;
        String listKey = "user:" + userId + ":pending_notifs";

        Boolean exists = redisTemplate.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.opsForList().rightPush(listKey, message);
        } else {
            System.out.println("Push Notification Sent to User " + userId + ": " + message);
            redisTemplate.opsForValue().set(cooldownKey, "1", 900, TimeUnit.SECONDS);
        }

    }
}