package com.zh.mathplatform.domain.comment.service;

import com.zh.mathplatform.domain.comment.entity.Comment;

/**
 * 评论领域服务接口
 */
public interface CommentDomainService {

    /**
     * 创建评论
     */
    Comment createComment(String content, Long postId, Long userId);

    /**
     * 创建回复评论
     */
    Comment createReplyComment(String content, Long postId, Long userId, Long parentId);

    /**
     * 更新评论
     */
    Comment updateComment(Comment comment, String content);

    /**
     * 删除评论
     */
    void deleteComment(Comment comment);

    /**
     * 处理评论点赞
     */
    void handleLike(Comment comment, boolean isLike);
}
