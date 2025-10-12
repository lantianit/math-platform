package com.zh.mathplatform.infrastructure.utils;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 锁管理器
 * 用于管理用户级别的锁对象，避免对字符串常量池加锁
 */
@Component
public class LockManager {
    
    private final Map<Long, Object> lockMap = new ConcurrentHashMap<>();
    
    /**
     * 获取锁对象
     */
    public Object getLock(Long key) {
        return lockMap.computeIfAbsent(key, k -> new Object());
    }
    
    /**
     * 移除锁对象
     */
    public void removeLock(Long key) {
        lockMap.remove(key);
    }
}

