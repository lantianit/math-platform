package com.zh.mathplatform.interfaces.dto.comment;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论查询请求
 */
@Data
public class CommentQueryRequest implements Serializable {

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 当前页
     */
    private long current = 1;

    /**
     * 页面大小
     */
    private long pageSize = 10;

    private static final long serialVersionUID = 1L;
}
