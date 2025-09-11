package com.zh.mathplatform.infrastructure.manager.upload.model.dto.file;

import lombok.Data;

@Data
public class UploadPictureResult {

    private String url;
    private String thumbnailUrl;
    private String picName;
    private Long picSize;
    private int picWidth;
    private int picHeight;
    private Double picScale;
    private String picFormat;
    private String picColor;
    // COS 对象键（用于后续审批复制/删除）
    private String objectKey;
    private String thumbnailKey;
    // 上传对象的校验信息
    private String etag;
    private String sha256;
}


