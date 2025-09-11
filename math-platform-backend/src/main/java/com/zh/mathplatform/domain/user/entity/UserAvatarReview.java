package com.zh.mathplatform.domain.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("user_avatar_review")
@Data
public class UserAvatarReview implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private String bucket;
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
    private Integer status; // AvatarReviewStatusEnum
    private String riskLabels; // json
    private Double machineScore;
    private String reason;
    private Long reviewerId;
    private Date submittedAt;
    private Date autoReviewedAt;
    private Date reviewedAt;
    private Date publishedAt;
    private String lastError;
}


