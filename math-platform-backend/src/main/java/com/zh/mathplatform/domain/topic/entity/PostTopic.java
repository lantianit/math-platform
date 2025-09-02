package com.zh.mathplatform.domain.topic.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子话题关联
 *
 * @TableName post_topic
 */
@TableName(value = "post_topic")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostTopic implements Serializable {

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
     * 话题ID
     */
    private Long topicId;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
