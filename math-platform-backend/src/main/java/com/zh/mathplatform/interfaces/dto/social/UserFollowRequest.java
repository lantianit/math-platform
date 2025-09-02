package com.zh.mathplatform.interfaces.dto.social;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户关注请求
 */
@Data
public class UserFollowRequest implements Serializable {

    /**
     * 被关注用户ID
     */
    @NotNull(message = "被关注用户ID不能为空")
    private Long followingId;

    private static final long serialVersionUID = 1L;
}
