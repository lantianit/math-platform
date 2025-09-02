package com.zh.mathplatform.domain.comment.service.impl;

import com.zh.mathplatform.domain.comment.entity.Comment;
import com.zh.mathplatform.domain.comment.service.CommentDomainService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 评论领域服务实现
 */
@Service
public class CommentDomainServiceImpl implements CommentDomainService {

    @Override
    public Comment createComment(String content, Long postId, Long userId) {
        // 校验参数
        Comment.validCommentAdd(content, postId, userId);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentId(null); // 不是回复
        comment.setLikeCount(0);
        comment.setStatus(1);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        comment.setIsDelete(0);

        return comment;
    }

    @Override
    public Comment createReplyComment(String content, Long postId, Long userId, Long parentId) {
        // 校验参数
        Comment.validCommentReply(content, postId, userId, parentId);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentId(parentId); // 回复的父评论ID
        comment.setLikeCount(0);
        comment.setStatus(1);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        comment.setIsDelete(0);

        return comment;
    }

    @Override
    public Comment updateComment(Comment comment, String content) {
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }

        // 校验参数
        Comment.validCommentUpdate(comment.getId(), content, comment.getUserId());

        comment.setContent(content);
        comment.setUpdateTime(new Date());

        return comment;
    }

    @Override
    public void deleteComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }
        comment.markAsDeleted();
        comment.setUpdateTime(new Date());
    }

    @Override
    public void handleLike(Comment comment, boolean isLike) {
        if (comment == null) {
            return;
        }
        if (isLike) {
            comment.increaseLikeCount();
        } else {
            comment.decreaseLikeCount();
        }
        comment.setUpdateTime(new Date());
    }
}
