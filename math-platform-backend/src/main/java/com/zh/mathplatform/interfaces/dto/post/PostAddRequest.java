package com.zh.mathplatform.interfaces.dto.post;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子发布请求
 */
@Data
public class PostAddRequest implements Serializable {

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
