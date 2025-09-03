package com.zh.mathplatform.domain.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论点赞
 *
 * @TableName comment_like
 */
@TableName(value = "comment_like")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentLike implements Serializable {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 评论ID
     */
    private Long commentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 类型：1-点赞 2-点踩
     */
    private Integer type;

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
}
