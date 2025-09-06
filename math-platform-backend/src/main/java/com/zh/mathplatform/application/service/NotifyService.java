package com.zh.mathplatform.application.service;

import com.zh.mathplatform.domain.notify.entity.Notification;

import java.util.List;

public interface NotifyService {
    void push(Notification notification);

    List<Notification> list(Long receiverId, Integer limit);

    Long unreadCount(Long receiverId);

    void markAllRead(Long receiverId);
}


