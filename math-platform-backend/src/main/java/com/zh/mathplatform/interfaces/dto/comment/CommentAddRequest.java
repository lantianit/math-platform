package com.zh.mathplatform.interfaces.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论添加请求
 */
@Data
public class CommentAddRequest implements Serializable {

    /**
     * 评论内容
     */
    private String content;

    /**
     * 帖子ID
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}
