package com.zh.mathplatform.interfaces.dto.social;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 帖子收藏请求
 */
@Data
public class PostFavouriteRequest implements Serializable {

    /**
     * 帖子ID
     */
    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    /**
     * 收藏夹ID（可选）
     */
    private Long folderId;

    private static final long serialVersionUID = 1L;
}
