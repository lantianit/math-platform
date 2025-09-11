package com.zh.mathplatform.interfaces.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class AvatarReviewSubmitRequest implements Serializable {
    private String objectKey;
    private String url;
    private String thumbnailKey;
    private String thumbnailUrl;
    private String etag;
    private String sha256;
    private Integer width;
    private Integer height;
    private String format;
    private Long size;
}


