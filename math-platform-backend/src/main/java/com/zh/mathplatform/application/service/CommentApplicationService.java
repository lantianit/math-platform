package com.zh.mathplatform.application.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.comment.entity.Comment;

import java.util.List;

/**
 * 评论应用服务接口
 */
public interface CommentApplicationService {

    /**
     * 添加评论
     */
    Comment addComment(String content, Long postId, Long userId);

    /**
     * 添加回复评论
     */
    Comment addReplyComment(String content, Long postId, Long userId, Long parentId);

    /**
     * 更新评论
     */
    Comment updateComment(Long commentId, String content, Long userId);

    /**
     * 删除评论
     */
    boolean deleteComment(Long commentId, Long userId);

    /**
     * 根据ID获取评论详情
     */
    Comment getCommentById(Long commentId);

    /**
     * 根据帖子ID分页获取评论列表
     */
    IPage<Comment> listCommentsByPostId(Page<Comment> page, Long postId);

    /**
     * 根据用户ID获取评论列表
     */
    List<Comment> listCommentsByUserId(Long userId);

    /**
     * 根据父评论ID获取回复列表
     */
    List<Comment> listRepliesByParentId(Long parentId);

    /**
     * 处理评论点赞
     */
    boolean toggleCommentLike(Long commentId, Long userId);

    /**
     * 获取帖子评论总数
     */
    Long getCommentCountByPostId(Long postId);
}
