package com.zh.mathplatform.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zh.mathplatform.application.service.NotifyService;
import com.zh.mathplatform.domain.notify.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NotifyServiceImpl implements NotifyService {

    private static final String LIST_KEY_PREFIX = "notify:list:"; // notify:list:{userId}
    private static final String UNREAD_KEY_PREFIX = "notify:unread:"; // 计数

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void push(Notification notification) {
        if (stringRedisTemplate == null || notification == null || notification.getReceiverId() == null) {
            return;
        }
        notification.setCreateTime(new Date());
        try {
            String key = LIST_KEY_PREFIX + notification.getReceiverId();
            stringRedisTemplate.opsForList().leftPush(key, objectMapper.writeValueAsString(notification));
            stringRedisTemplate.opsForValue().increment(UNREAD_KEY_PREFIX + notification.getReceiverId());
            // 可设置列表长度上限
            stringRedisTemplate.opsForList().trim(key, 0, 500);
        } catch (JsonProcessingException ignored) { }
    }

    @Override
    public List<Notification> list(Long receiverId, Integer limit) {
        List<Notification> result = new ArrayList<>();
        if (stringRedisTemplate == null || receiverId == null) return result;
        String key = LIST_KEY_PREFIX + receiverId;
        List<String> vals = stringRedisTemplate.opsForList().range(key, 0, limit == null ? 50 : Math.max(0, limit - 1));
        if (vals == null) return result;
        for (String v : vals) {
            try {
                result.add(objectMapper.readValue(v, Notification.class));
            } catch (Exception ignored) { }
        }
        return result;
    }

    @Override
    public Long unreadCount(Long receiverId) {
        if (stringRedisTemplate == null || receiverId == null) return 0L;
        String v = stringRedisTemplate.opsForValue().get(UNREAD_KEY_PREFIX + receiverId);
        try { return v == null ? 0L : Long.parseLong(v); } catch (Exception e) { return 0L; }
    }

    @Override
    public void markAllRead(Long receiverId) {
        if (stringRedisTemplate == null || receiverId == null) return;
        stringRedisTemplate.opsForValue().set(UNREAD_KEY_PREFIX + receiverId, "0");
    }
}


