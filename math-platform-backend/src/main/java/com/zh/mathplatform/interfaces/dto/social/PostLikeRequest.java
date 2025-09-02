package com.zh.mathplatform.interfaces.dto.social;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 帖子点赞请求
 */
@Data
public class PostLikeRequest implements Serializable {

    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    /**
     * 点赞类型：1-点赞 2-点踩
     */
    private Integer type = 1;

    private static final long serialVersionUID = 1L;
}
