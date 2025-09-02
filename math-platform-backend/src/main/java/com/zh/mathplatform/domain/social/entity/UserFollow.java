package com.zh.mathplatform.domain.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户关注
 *
 * @TableName user_follow
 */
@TableName(value = "user_follow")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFollow implements Serializable {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关注者ID
     */
    private Long followerId;

    /**
     * 被关注者ID
     */
    private Long followingId;

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
     * 校验关注参数
     */
    public static void validUserFollow(Long followerId, Long followingId) {
        if (followerId == null || followerId <= 0) {
            throw new IllegalArgumentException("关注者ID无效");
        }
        if (followingId == null || followingId <= 0) {
            throw new IllegalArgumentException("被关注者ID无效");
        }
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("不能关注自己");
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
