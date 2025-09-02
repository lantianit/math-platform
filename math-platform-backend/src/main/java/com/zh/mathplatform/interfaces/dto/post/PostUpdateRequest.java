package com.zh.mathplatform.interfaces.dto.post;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子更新请求
 */
@Data
public class PostUpdateRequest implements Serializable {

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
     * 分类：tech/question/project
     */
    private String category;

    /**
     * 标签，JSON格式
     */
    private String tags;

    /**
     * 图片URL，JSON格式
     */
    private String images;

    private static final long serialVersionUID = 1L;
}
