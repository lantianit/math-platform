package com.zh.mathplatform.domain.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子收藏
 *
 * @TableName post_favourite
 */
@TableName(value = "post_favourite")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostFavourite implements Serializable {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收藏夹ID
     */
    private Long folderId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除（无物理列，仅用于内存状态）
     */
    @TableField(exist = false)
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 校验收藏参数
     */
    public static void validPostFavourite(Long postId, Long userId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("帖子ID无效");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID无效");
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
