package com.zh.mathplatform.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 图片压缩配置类
 * 管理图片压缩相关参数
 * 
 * @author zh
 */
@Configuration
@ConfigurationProperties(prefix = "picture.compression")
@Data
public class PictureCompressionConfig {

    /**
     * 是否启用图片压缩
     */
    private boolean enabled = true;

    /**
     * 压缩图质量（1-100）
     * 推荐值：85（平衡画质和文件大小）
     */
    private int quality = 85;

    /**
     * 缩略图质量（1-100）
     * 推荐值：75（缩略图对画质要求较低）
     */
    private int thumbnailQuality = 75;

    /**
     * 缩略图最大宽度
     */
    private int thumbnailMaxWidth = 256;

    /**
     * 缩略图最大高度
     */
    private int thumbnailMaxHeight = 256;

    /**
     * 生成缩略图的最小文件大小（字节）
     * 小于此大小的图片不生成缩略图
     */
    private long thumbnailMinFileSize = 2048; // 2KB

    /**
     * 目标压缩格式
     * 支持：webp, avif
     */
    private String targetFormat = "webp";

    /**
     * 是否保留原图
     * true: 保留原图和压缩图
     * false: 只保留压缩图
     */
    private boolean keepOriginal = false;

    /**
     * 是否启用渐进式JPEG（对JPEG格式有效）
     */
    private boolean progressiveJpeg = true;

    /**
     * 获取压缩规则
     */
    public String getCompressionRule() {
        if (!enabled) {
            return null;
        }
        return String.format("imageMogr2/format/%s/quality/%d%s", 
            targetFormat, quality, progressiveJpeg ? "/interlace/1" : "");
    }

    /**
     * 获取缩略图规则
     */
    public String getThumbnailRule() {
        if (!enabled) {
            return null;
        }
        return String.format("imageMogr2/thumbnail/%dx%d>/format/%s/quality/%d", 
            thumbnailMaxWidth, thumbnailMaxHeight, targetFormat, thumbnailQuality);
    }

    /**
     * 获取缩略图文件扩展名
     */
    public String getThumbnailExtension() {
        return "." + targetFormat;
    }

    /**
     * 获取压缩图文件扩展名
     */
    public String getCompressionExtension() {
        return "." + targetFormat;
    }
}
