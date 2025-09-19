package com.zh.mathplatform.domain.wallpaper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学习加油壁纸实体
 */
@Data
@TableName("wallpaper")
public class Wallpaper implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 壁纸名称
     */
    private String name;

    /**
     * 壁纸描述
     */
    private String description;

    /**
     * 壁纸URL
     */
    private String url;

    /**
     * 壁纸缩略图URL
     */
    @TableField("thumbnail_url")
    private String thumbnailUrl;

    /**
     * 壁纸分类（学习、励志、奋斗等）
     */
    private String category;

    /**
     * 壁纸标签（JSON数组格式）
     */
    private String tags;

    /**
     * 图片宽度
     */
    private Integer width;

    /**
     * 图片高度
     */
    private Integer height;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 下载次数
     */
    @TableField("download_count")
    private Integer downloadCount;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 状态（0-正常，1-隐藏）
     */
    private Integer status;

    /**
     * 创建用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
