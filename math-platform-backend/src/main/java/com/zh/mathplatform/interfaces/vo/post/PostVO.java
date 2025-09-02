package com.zh.mathplatform.interfaces.vo.post;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子视图对象
 */
@Data
public class PostVO implements Serializable {

    /**
     * 帖子ID
     */
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
     * 发布者名称
     */
    private String userName;

    /**
     * 发布者头像
     */
    private String userAvatar;

    /**
     * 分类：tech/question/project
     */
    private String category;

    /**
     * 标签列表
     */
    private java.util.List<String> tags;

    /**
     * 图片URL列表
     */
    private java.util.List<String> images;

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
     * 是否已点赞
     */
    private Boolean isLiked;

    /**
     * 是否已收藏
     */
    private Boolean isFavourited;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
