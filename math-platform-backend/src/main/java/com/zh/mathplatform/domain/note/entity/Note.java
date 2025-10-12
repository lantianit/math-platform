package com.zh.mathplatform.domain.note.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 笔记实体
 */
@TableName(value = "note")
@Data
public class Note implements Serializable {
    
    /**
     * 笔记ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 笔记标题
     */
    private String title;

    /**
     * 笔记内容（Markdown格式）
     */
    private String content;

    /**
     * 内容摘要
     */
    private String contentSummary;

    /**
     * 创建者ID
     */
    private Long userId;

    /**
     * 空间ID（为空表示公共笔记）
     */
    private Long spaceId;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签，JSON格式
     */
    private String tags;

    /**
     * 附件URL，JSON格式
     */
    private String attachments;

    /**
     * 附件总大小（字节）
     */
    private Long attachmentSize;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer favouriteCount;

    /**
     * 状态：0-草稿 1-已发布 2-审核中
     */
    private Integer status;

    /**
     * 审核状态：0-待审核 1-通过 2-拒绝
     */
    private Integer auditStatus;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核人ID
     */
    private Long auditUserId;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

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
}

