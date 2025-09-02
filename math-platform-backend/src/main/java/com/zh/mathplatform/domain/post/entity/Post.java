package com.zh.mathplatform.domain.post.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子
 *
 * @TableName post
 */
@TableName(value = "post")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post implements Serializable {

    /**
     * 帖子ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 发布者ID
     */
    private Long userId;

    /**
     * 分类：tech/question/project
     */
    private String category;

    /**
     * 标签，JSON格式
     */
    private String tags;

    /**
     * 图片URL，JSON格式
     */
    private String images;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 收藏数
     */
    private Integer favouriteCount;

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
     * 校验帖子发布参数
     */
    public static void validPostAdd(String title, String content, Long userId) {
        if (StrUtil.hasBlank(title, content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题或内容不能为空");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        if (title.length() > 200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题长度不能超过200字符");
        }
        if (content.length() > 10000) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容长度不能超过10000字符");
        }
    }

    /**
     * 校验帖子更新参数
     */
    public static void validPostUpdate(Long id, String title, String content, Long userId) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子ID无效");
        }
        validPostAdd(title, content, userId);
    }

    /**
     * 增加浏览量
     */
    public void increaseViewCount() {
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
        this.viewCount++;
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
     * 增加评论数
     */
    public void increaseCommentCount() {
        if (this.commentCount == null) {
            this.commentCount = 0;
        }
        this.commentCount++;
    }

    /**
     * 减少评论数
     */
    public void decreaseCommentCount() {
        if (this.commentCount != null && this.commentCount > 0) {
            this.commentCount--;
        }
    }

    /**
     * 增加收藏数
     */
    public void increaseFavouriteCount() {
        if (this.favouriteCount == null) {
            this.favouriteCount = 0;
        }
        this.favouriteCount++;
    }

    /**
     * 减少收藏数
     */
    public void decreaseFavouriteCount() {
        if (this.favouriteCount != null && this.favouriteCount > 0) {
            this.favouriteCount--;
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
}
