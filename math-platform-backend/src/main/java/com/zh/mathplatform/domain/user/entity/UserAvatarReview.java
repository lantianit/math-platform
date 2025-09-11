package com.zh.mathplatform.domain.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
    @TableField("user_id")
    private Long userId;
    private String bucket;
    @TableField("object_key")
    private String objectKey;
    private String url;
    @TableField("thumbnail_key")
    private String thumbnailKey;
    @TableField("thumbnail_url")
    private String thumbnailUrl;
    private String etag;
    private String sha256;
    private Integer width;
    private Integer height;
    private String format;
    private Long size;
    private Integer status; // AvatarReviewStatusEnum
    @TableField("risk_labels")
    private String riskLabels; // json
    @TableField("machine_score")
    private Double machineScore;
    private String reason;
    @TableField("reviewer_id")
    private Long reviewerId;
    @TableField("submitted_at")
    private Date submittedAt;
    @TableField("auto_reviewed_at")
    private Date autoReviewedAt;
    @TableField("reviewed_at")
    private Date reviewedAt;
    @TableField("published_at")
    private Date publishedAt;
    @TableField("last_error")
    private String lastError;
}


