package com.yash.BackendAssignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    RedisTemplate redisTemplate;
    @GetMapping("/test-redis")
    public String testRedis() {
        redisTemplate.opsForValue().set("test", "hello");
        return (String) redisTemplate.opsForValue().get("test");
    }
}
