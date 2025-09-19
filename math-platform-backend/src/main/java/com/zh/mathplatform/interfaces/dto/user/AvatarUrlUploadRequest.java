package com.zh.mathplatform.interfaces.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 通过 URL 上传头像的请求
 */
@Data
public class AvatarUrlUploadRequest {

    /**
     * 图片 URL（http/https）
     */
    @NotBlank(message = "图片链接不能为空")
    private String url;
}


