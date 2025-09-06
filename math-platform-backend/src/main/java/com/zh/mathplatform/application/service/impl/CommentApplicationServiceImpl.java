package com.zh.mathplatform.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.CommentApplicationService;
import com.zh.mathplatform.domain.comment.entity.Comment;
import com.zh.mathplatform.domain.comment.repository.CommentRepository;
import com.zh.mathplatform.domain.comment.service.CommentDomainService;
import com.zh.mathplatform.domain.post.entity.Post;
import com.zh.mathplatform.domain.post.repository.PostRepository;
import com.zh.mathplatform.domain.social.service.SocialDomainService;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.application.service.NotifyService;
import com.zh.mathplatform.domain.notify.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论应用服务实现
 */
@Service
public class CommentApplicationServiceImpl implements CommentApplicationService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentDomainService commentDomainService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SocialDomainService socialDomainService;

    @Autowired(required = false)
    private NotifyService notifyService;

    @Override
    @Transactional
    public Comment addComment(String content, Long postId, Long userId) {
        // 检查帖子是否存在
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }

        // 创建评论
        Comment comment = commentDomainService.createComment(content, postId, userId);

        // 保存到数据库
        boolean success = commentRepository.save(comment);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发布评论失败");
        }

        // 增加帖子评论数
        postRepository.increaseCommentCount(postId);

        // 推送通知给帖子作者
        try {
            Notification n = new Notification();
            n.setReceiverId(post.getUserId());
            n.setType("comment");
            n.setContent("有人评论了你的帖子: " + (content.length() > 20 ? content.substring(0, 20) + "..." : content));
            n.setExtra("{\"postId\":" + postId + "}");
            notifyService.push(n);
        } catch (Exception ignored) {}

        return comment;
    }

    @Override
    @Transactional
    public Comment addReplyComment(String content, Long postId, Long userId, Long parentId) {
        // 检查帖子是否存在
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }

        // 检查父评论是否存在
        Comment parentComment = commentRepository.findById(parentId);
        if (parentComment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "父评论不存在");
        }

        // 创建回复评论
        Comment replyComment = commentDomainService.createReplyComment(content, postId, userId, parentId);

        // 保存到数据库
        boolean success = commentRepository.save(replyComment);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发布回复失败");
        }

        // 增加帖子评论数
        postRepository.increaseCommentCount(postId);

        // 通知父评论作者
        try {
            Notification n = new Notification();
            n.setReceiverId(parentComment.getUserId());
            n.setType("reply");
            n.setContent("有人回复了你的评论: " + (content.length() > 20 ? content.substring(0, 20) + "..." : content));
            n.setExtra("{\"postId\":" + postId + ",\"parentId\":" + parentId + "}");
            notifyService.push(n);
        } catch (Exception ignored) {}

        return replyComment;
    }

    @Override
    @Transactional
    public Comment updateComment(Long commentId, String content, Long userId) {
        // 获取原评论
        Comment existingComment = commentRepository.findById(commentId);
        if (existingComment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        }

        // 检查权限
        if (!existingComment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限编辑此评论");
        }

        // 更新评论
        Comment updatedComment = commentDomainService.updateComment(existingComment, content);

        // 保存到数据库
        boolean success = commentRepository.updateById(updatedComment);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新评论失败");
        }

        return updatedComment;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        // 获取评论
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        }

        // 检查权限
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除此评论");
        }

        // 删除评论
        commentDomainService.deleteComment(comment);

        // 保存到数据库
        boolean success = commentRepository.updateById(comment);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除评论失败");
        }

        // 减少帖子评论数
        postRepository.decreaseCommentCount(comment.getPostId());

        return true;
    }

    @Override
    public Comment getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        }
        return comment;
    }

    @Override
    public IPage<Comment> listCommentsByPostId(Page<Comment> page, Long postId) {
        // 检查帖子是否存在
        Post post = postRepository.findById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        }

        return commentRepository.findByPostIdPage(page, postId);
    }

    @Override
    public List<Comment> listCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    @Override
    public List<Comment> listRepliesByParentId(Long parentId) {
        return commentRepository.findByParentId(parentId);
    }

    @Override
    @Transactional
    public boolean toggleCommentLike(Long commentId, Long userId) {
        // 检查评论是否存在
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        }

        // 处理点赞（这里简化处理，直接在Comment实体上操作）
        // 实际应该使用专门的CommentLike实体
        boolean isLiked = socialDomainService.togglePostLike(commentId, userId); // 复用PostLike逻辑，实际应该有单独的CommentLike

        // 更新评论点赞数
        if (isLiked) {
            commentRepository.increaseLikeCount(commentId);
            commentDomainService.handleLike(comment, true);
        } else {
            commentRepository.decreaseLikeCount(commentId);
            commentDomainService.handleLike(comment, false);
        }

        return isLiked;
    }

    @Override
    public Long getCommentCountByPostId(Long postId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
               .eq(Comment::getIsDelete, 0);

        return commentRepository.findByCondition(wrapper).stream().count();
    }
}
