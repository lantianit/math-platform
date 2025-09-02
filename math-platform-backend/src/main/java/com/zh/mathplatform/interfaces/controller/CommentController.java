package com.zh.mathplatform.interfaces.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.CommentApplicationService;
import com.zh.mathplatform.domain.comment.entity.Comment;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评论接口
 */
@RestController
@RequestMapping("/api/comment")
@Slf4j
public class CommentController {

    @Autowired
    private CommentApplicationService commentApplicationService;

    /**
     * 添加评论
     */
    @PostMapping("/add")
    public BaseResponse<Long> addComment(@RequestBody CommentAddRequest commentAddRequest, HttpServletRequest request) {
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        Comment comment = commentApplicationService.addComment(
            commentAddRequest.getContent(),
            commentAddRequest.getPostId(),
            userId
        );

        return ResultUtils.success(comment.getId());
    }

    /**
     * 添加回复评论
     */
    @PostMapping("/reply")
    public BaseResponse<Long> addReplyComment(@RequestBody CommentReplyRequest commentReplyRequest, HttpServletRequest request) {
        if (commentReplyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        Comment replyComment = commentApplicationService.addReplyComment(
            commentReplyRequest.getContent(),
            commentReplyRequest.getPostId(),
            userId,
            commentReplyRequest.getParentId()
        );

        return ResultUtils.success(replyComment.getId());
    }

    /**
     * 更新评论
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateComment(@RequestBody CommentUpdateRequest commentUpdateRequest, HttpServletRequest request) {
        if (commentUpdateRequest == null || commentUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        Comment comment = commentApplicationService.updateComment(
            commentUpdateRequest.getId(),
            commentUpdateRequest.getContent(),
            userId
        );

        return ResultUtils.success(true);
    }

    /**
     * 删除评论
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComment(@RequestBody CommentDeleteRequest commentDeleteRequest, HttpServletRequest request) {
        if (commentDeleteRequest == null || commentDeleteRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean result = commentApplicationService.deleteComment(commentDeleteRequest.getId(), userId);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取评论详情
     */
    @GetMapping("/get")
    public BaseResponse<Comment> getCommentById(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Comment comment = commentApplicationService.getCommentById(id);
        return ResultUtils.success(comment);
    }

    /**
     * 分页获取帖子评论列表
     */
    @PostMapping("/list")
    public BaseResponse<IPage<Comment>> listCommentsByPostId(@RequestBody CommentQueryRequest commentQueryRequest) {
        if (commentQueryRequest == null || commentQueryRequest.getPostId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long current = commentQueryRequest.getCurrent();
        long size = commentQueryRequest.getPageSize();

        Page<Comment> page = new Page<>(current, size);
        IPage<Comment> commentPage = commentApplicationService.listCommentsByPostId(
            page,
            commentQueryRequest.getPostId()
        );

        return ResultUtils.success(commentPage);
    }

    /**
     * 获取评论回复列表
     */
    @GetMapping("/replies")
    public BaseResponse<List<Comment>> listRepliesByParentId(@RequestParam Long parentId) {
        if (parentId == null || parentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<Comment> replies = commentApplicationService.listRepliesByParentId(parentId);
        return ResultUtils.success(replies);
    }

    /**
     * 处理评论点赞
     */
    @PostMapping("/like")
    public BaseResponse<Boolean> toggleCommentLike(@RequestBody CommentLikeRequest commentLikeRequest, HttpServletRequest request) {
        if (commentLikeRequest == null || commentLikeRequest.getCommentId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Long userId = getLoginUserId(request);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        boolean isLiked = commentApplicationService.toggleCommentLike(commentLikeRequest.getCommentId(), userId);
        return ResultUtils.success(isLiked);
    }

    /**
     * 获取帖子评论总数
     */
    @GetMapping("/count")
    public BaseResponse<Long> getCommentCountByPostId(@RequestParam Long postId) {
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long count = commentApplicationService.getCommentCountByPostId(postId);
        return ResultUtils.success(count);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getLoginUserId(HttpServletRequest request) {
        // TODO: 从session或token中获取用户ID
        // 这里需要根据实际的用户认证机制来实现
        return 1L; // 临时返回，实际需要从认证信息中获取
    }

    // ===== 请求类定义 =====

    /**
     * 评论添加请求
     */
    public static class CommentAddRequest {
        private String content;
        private Long postId;

        // getters and setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
    }

    /**
     * 评论回复请求
     */
    public static class CommentReplyRequest {
        private String content;
        private Long postId;
        private Long parentId;

        // getters and setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
    }

    /**
     * 评论更新请求
     */
    public static class CommentUpdateRequest {
        private Long id;
        private String content;

        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    /**
     * 评论删除请求
     */
    public static class CommentDeleteRequest {
        private Long id;

        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    /**
     * 评论查询请求
     */
    public static class CommentQueryRequest {
        private Long postId;
        private long current = 1;
        private long pageSize = 10;

        // getters and setters
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public long getCurrent() { return current; }
        public void setCurrent(long current) { this.current = current; }
        public long getPageSize() { return pageSize; }
        public void setPageSize(long pageSize) { this.pageSize = pageSize; }
    }

    /**
     * 评论点赞请求
     */
    public static class CommentLikeRequest {
        private Long commentId;

        // getters and setters
        public Long getCommentId() { return commentId; }
        public void setCommentId(Long commentId) { this.commentId = commentId; }
    }
}
