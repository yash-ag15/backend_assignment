package com.yash.BackendAssignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String,Object>redisTemplate;
    public String key(Long postId){
        return "postId" + postId + ":score";
    }
    public void incrementScore(Long postId, int inc) {
        redisTemplate.opsForValue().increment(key(postId), inc);
    }
    public Long getScore(Long postId) {
        Object val = redisTemplate.opsForValue().get(key(postId));
        return val == null ? 0L : Long.valueOf(val.toString());
    }
}
