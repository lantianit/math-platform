package com.zh.mathplatform.interfaces.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论更新请求
 */
@Data
public class CommentUpdateRequest implements Serializable {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    private static final long serialVersionUID = 1L;
}
