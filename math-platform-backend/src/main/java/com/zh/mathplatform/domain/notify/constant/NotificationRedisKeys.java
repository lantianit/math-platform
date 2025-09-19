package com.zh.mathplatform.domain.notify.constant;

import com.zh.mathplatform.infrastructure.config.RedisKeyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通知领域 Redis Key 管理
 * 符合 DDD 架构 - 领域内部常量管理
 * 
 * @author zh
 */
@Component
public class NotificationRedisKeys {

    private static final String DOMAIN = "notify";

    @Autowired
    private RedisKeyConfig redisKeyConfig;

    /**
     * 获取用户通知列表 Key
     * Key: math-platform:dev:notify:list:{userId}
     */
    public String getUserNotificationList(Long userId) {
        return redisKeyConfig.buildKeyWithId(DOMAIN, "list", userId);
    }

    /**
     * 获取用户未读通知计数 Key
     * Key: math-platform:dev:notify:unread:{userId}
     */
    public String getUserUnreadCount(Long userId) {
        return redisKeyConfig.buildKeyWithId(DOMAIN, "unread", userId);
    }

    /**
     * 获取全局广播通知 Key
     * Key: math-platform:dev:notify:broadcast
     */
    public String getGlobalBroadcast() {
        return redisKeyConfig.buildKey(DOMAIN, "broadcast");
    }

    /**
     * 获取通知推送队列 Key
     * Key: math-platform:dev:notify:push-queue
     */
    public String getPushQueue() {
        return redisKeyConfig.buildKey(DOMAIN, "push-queue");
    }

    /**
     * 获取用户通知设置 Key
     * Key: math-platform:dev:notify:settings:{userId}
     */
    public String getUserNotificationSettings(Long userId) {
        return redisKeyConfig.buildKeyWithId(DOMAIN, "settings", userId);
    }
}
