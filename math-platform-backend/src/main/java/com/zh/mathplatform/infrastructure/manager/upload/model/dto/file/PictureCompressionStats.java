package com.zh.mathplatform.infrastructure.manager.upload.model.dto.file;

import lombok.Data;

/**
 * 图片压缩统计信息
 * 
 * @author zh
 */
@Data
public class PictureCompressionStats {
    
    /**
     * 原始文件大小（字节）
     */
    private long originalSize;
    
    /**
     * 压缩后文件大小（字节）
     */
    private long compressedSize;
    
    /**
     * 压缩率（百分比）
     */
    private double compressionRatio;
    
    /**
     * 原始格式
     */
    private String originalFormat;
    
    /**
     * 压缩后格式
     */
    private String compressedFormat;
    
    /**
     * 是否生成了缩略图
     */
    private boolean hasThumbnail;
    
    /**
     * 缩略图大小（字节）
     */
    private long thumbnailSize;
    
    /**
     * 处理耗时（毫秒）
     */
    private long processingTime;
    
    /**
     * 计算压缩率
     */
    public void calculateCompressionRatio() {
        if (originalSize > 0) {
            this.compressionRatio = (1 - (double) compressedSize / originalSize) * 100;
        } else {
            this.compressionRatio = 0;
        }
    }
    
    /**
     * 获取节省的空间（字节）
     */
    public long getSavedSpace() {
        return originalSize - compressedSize;
    }
    
    /**
     * 获取格式化的压缩率字符串
     */
    public String getFormattedCompressionRatio() {
        return String.format("%.1f%%", compressionRatio);
    }
    
    /**
     * 获取格式化的文件大小
     */
    public String getFormattedOriginalSize() {
        return formatFileSize(originalSize);
    }
    
    public String getFormattedCompressedSize() {
        return formatFileSize(compressedSize);
    }
    
    public String getFormattedSavedSpace() {
        return formatFileSize(getSavedSpace());
    }
    
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }
}
