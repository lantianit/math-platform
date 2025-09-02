package com.zh.mathplatform.domain.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户消息
 *
 * @TableName user_message
 */
@TableName(value = "user_message")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMessage implements Serializable {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 发送者ID（系统消息为null）
     */
    private Long fromUserId;

    /**
     * 接收者ID
     */
    private Long toUserId;

    /**
     * 消息类型：1-系统消息 2-点赞 3-评论 4-关注 5-私信
     */
    private Integer type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 关联ID（如帖子ID、评论ID等）
     */
    private Long relatedId;

    /**
     * 关联类型
     */
    private String relatedType;

    /**
     * 是否已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
