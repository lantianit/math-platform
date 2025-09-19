package com.zh.mathplatform.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zh.mathplatform.application.service.NotifyService;
import com.zh.mathplatform.domain.notify.constant.NotificationRedisKeys;
import com.zh.mathplatform.domain.notify.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private NotificationRedisKeys notificationRedisKeys;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void push(Notification notification) {
        if (stringRedisTemplate == null || notification == null || notification.getReceiverId() == null) {
            return;
        }
        notification.setCreateTime(new Date());
        try {
            String listKey = notificationRedisKeys.getUserNotificationList(notification.getReceiverId());
            String countKey = notificationRedisKeys.getUserUnreadCount(notification.getReceiverId());
            
            stringRedisTemplate.opsForList().leftPush(listKey, objectMapper.writeValueAsString(notification));
            stringRedisTemplate.opsForValue().increment(countKey);
            // 可设置列表长度上限
            stringRedisTemplate.opsForList().trim(listKey, 0, 500);
            
            log.debug("推送通知成功: userId={}, type={}", notification.getReceiverId(), notification.getType());
        } catch (JsonProcessingException e) {
            log.error("推送通知失败: userId={}, 序列化错误: {}", notification.getReceiverId(), e.getMessage());
        }
    }

    @Override
    public List<Notification> list(Long receiverId, Integer limit) {
        List<Notification> result = new ArrayList<>();
        if (stringRedisTemplate == null || receiverId == null) return result;
        
        String key = notificationRedisKeys.getUserNotificationList(receiverId);
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
        
        String key = notificationRedisKeys.getUserUnreadCount(receiverId);
        String v = stringRedisTemplate.opsForValue().get(key);
        try { 
            return v == null ? 0L : Long.parseLong(v); 
        } catch (Exception e) { 
            log.warn("获取未读计数失败: userId={}, 错误: {}", receiverId, e.getMessage());
            return 0L; 
        }
    }

    @Override
    public void markAllRead(Long receiverId) {
        if (stringRedisTemplate == null || receiverId == null) return;
        
        String key = notificationRedisKeys.getUserUnreadCount(receiverId);
        stringRedisTemplate.opsForValue().set(key, "0");
        log.debug("标记全部已读: userId={}", receiverId);
    }
}


