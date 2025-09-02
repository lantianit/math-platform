package com.zh.mathplatform.interfaces.dto.post;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子删除请求
 */
@Data
public class PostDeleteRequest implements Serializable {

    /**
     * 帖子ID
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
