package com.yash.BackendAssignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class Schedular {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 300000)
    public void processNotifications() {

        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");

        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {

            List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);

            if (messages != null && !messages.isEmpty()) {

                int count = messages.size();

                String userId = key.split(":")[1];

                String firstMessage = messages.get(0).toString();

                String botInfo = firstMessage.split(" ")[1];

                System.out.println(
                        "Summarized Push Notification: Bot " + botInfo +
                                " and " + (count - 1) + " others interacted with your posts."
                );

                redisTemplate.delete(key);
            }
        }
    }
}