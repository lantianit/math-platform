package com.zh.mathplatform.domain.social.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 收藏夹
 *
 * @TableName favourite_folder
 */
@TableName(value = "favourite_folder")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FavouriteFolder implements Serializable {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收藏夹名称
     */
    private String name;

    /**
     * 收藏夹描述
     */
    private String description;

    /**
     * 是否公开：0-私有 1-公开
     */
    private Integer isPublic;

    /**
     * 帖子数量
     */
    private Integer postCount;

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
