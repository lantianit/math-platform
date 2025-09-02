package com.zh.mathplatform.domain.comment.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论
 *
 * @TableName comment
 */
@TableName(value = "comment")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment implements Serializable {

    /**
     * 评论ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 评论者ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 根评论ID（用于楼层显示）
     */
    private Long rootId;

    /**
     * 回复目标用户ID
     */
    private Long replyToId;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 状态：0-删除 1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 校验评论发布参数
     */
    public static void validCommentAdd(String content, Long postId, Long userId) {
        if (StrUtil.hasBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }
        if (postId == null || postId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子ID无效");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        if (content.length() > 1000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容长度不能超过1000字符");
        }
    }

    /**
     * 校验评论回复参数
     */
    public static void validCommentReply(String content, Long postId, Long userId, Long parentId) {
        validCommentAdd(content, postId, userId);
        if (parentId == null || parentId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "父评论ID无效");
        }
    }

    /**
     * 校验评论更新参数
     */
    public static void validCommentUpdate(Long id, String content, Long userId) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论ID无效");
        }
        if (StrUtil.hasBlank(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        if (content.length() > 1000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容长度不能超过1000字符");
        }
    }

    /**
     * 增加点赞数
     */
    public void increaseLikeCount() {
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        this.likeCount++;
    }

    /**
     * 减少点赞数
     */
    public void decreaseLikeCount() {
        if (this.likeCount != null && this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * 是否已删除
     */
    public boolean isDeleted() {
        return this.isDelete != null && this.isDelete == 1;
    }

    /**
     * 标记为删除
     */
    public void markAsDeleted() {
        this.isDelete = 1;
    }

    /**
     * 是否为回复评论
     */
    public boolean isReply() {
        return this.parentId != null && this.parentId > 0;
    }

    /**
     * 获取根评论ID
     */
    public Long getRootCommentId() {
        return this.parentId != null ? this.parentId : this.id;
    }
}
