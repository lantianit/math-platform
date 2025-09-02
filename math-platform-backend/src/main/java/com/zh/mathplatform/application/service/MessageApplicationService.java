package com.zh.mathplatform.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.message.entity.UserMessage;

/**
 * 消息应用服务接口
 */
public interface MessageApplicationService {

    /**
     * 发送系统消息
     */
    boolean sendSystemMessage(Long toUserId, String title, String content);

    /**
     * 发送点赞消息
     */
    boolean sendLikeMessage(Long fromUserId, Long toUserId, Long relatedId, String relatedType);

    /**
     * 发送评论消息
     */
    boolean sendCommentMessage(Long fromUserId, Long toUserId, Long relatedId, String content);

    /**
     * 发送关注消息
     */
    boolean sendFollowMessage(Long fromUserId, Long toUserId);

    /**
     * 分页获取用户消息
     */
    IPage<UserMessage> getUserMessages(Page<UserMessage> page, Long userId, Integer type);

    /**
     * 标记消息为已读
     */
    boolean markAsRead(Long messageId, Long userId);

    /**
     * 批量标记消息为已读
     */
    boolean markAllAsRead(Long userId, Integer type);

    /**
     * 获取未读消息数量
     */
    Long getUnreadCount(Long userId, Integer type);

    /**
     * 删除消息
     */
    boolean deleteMessage(Long messageId, Long userId);
}
