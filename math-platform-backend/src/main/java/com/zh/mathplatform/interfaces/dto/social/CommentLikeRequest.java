package com.zh.mathplatform.interfaces.dto.social;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 评论点赞请求
 */
@Data
public class CommentLikeRequest implements Serializable {

    /**
     * 评论ID
     */
    @NotNull(message = "评论ID不能为空")
    private Long commentId;

    /**
     * 点赞类型：1-点赞 2-点踩
     */
    private Integer type = 1;

    private static final long serialVersionUID = 1L;
}
