package com.zh.mathplatform.domain.topic.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 话题
 *
 * @TableName topic
 */
@TableName(value = "topic")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Topic implements Serializable {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 话题名称
     */
    private String name;

    /**
     * 话题描述
     */
    private String description;

    /**
     * 话题头像
     */
    private String avatar;

    /**
     * 帖子数量
     */
    private Integer postCount;

    /**
     * 关注数量
     */
    private Integer followCount;

    /**
     * 是否热门
     */
    private Integer isHot;

    /**
     * 状态：0-禁用 1-正常
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
}
