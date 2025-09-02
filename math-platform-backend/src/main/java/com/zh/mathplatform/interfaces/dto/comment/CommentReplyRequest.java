package com.zh.mathplatform.interfaces.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论回复请求
 */
@Data
public class CommentReplyRequest implements Serializable {

    /**
     * 回复内容
     */
    private String content;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 父评论ID
     */
    private Long parentId;

    private static final long serialVersionUID = 1L;
}
