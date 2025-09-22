package com.zh.mathplatform.infrastructure.manager.upload;

import com.zh.mathplatform.infrastructure.manager.upload.model.dto.file.PictureCompressionStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 图片压缩统计管理器
 * 用于收集和管理图片压缩的统计信息
 * 
 * @author zh
 */
@Component
@Slf4j
public class PictureCompressionStatsManager {
    
    // 总上传次数
    private final LongAdder totalUploads = new LongAdder();
    
    // 总压缩次数
    private final LongAdder totalCompressions = new LongAdder();
    
    // 总节省空间（字节）
    private final AtomicLong totalSpaceSaved = new AtomicLong(0);
    
    // 总处理时间（毫秒）
    private final AtomicLong totalProcessingTime = new AtomicLong(0);
    
    /**
     * 记录压缩统计信息
     */
    public void recordCompressionStats(PictureCompressionStats stats) {
        if (stats == null) {
            return;
        }
        
        totalUploads.increment();
        
        if (stats.getCompressionRatio() > 0) {
            totalCompressions.increment();
            totalSpaceSaved.addAndGet(stats.getSavedSpace());
        }
        
        totalProcessingTime.addAndGet(stats.getProcessingTime());
        
        // 记录详细日志用于监控
        log.debug("压缩统计 - 格式转换: {} -> {}, 压缩率: {}, 节省空间: {}, 处理时间: {}ms",
            stats.getOriginalFormat(),
            stats.getCompressedFormat(),
            stats.getFormattedCompressionRatio(),
            stats.getFormattedSavedSpace(),
            stats.getProcessingTime());
    }
    
    /**
     * 获取总体统计信息
     */
    public CompressionSummaryStats getSummaryStats() {
        CompressionSummaryStats summary = new CompressionSummaryStats();
        summary.setTotalUploads(totalUploads.sum());
        summary.setTotalCompressions(totalCompressions.sum());
        summary.setTotalSpaceSaved(totalSpaceSaved.get());
        summary.setTotalProcessingTime(totalProcessingTime.get());
        
        // 计算平均值
        if (summary.getTotalCompressions() > 0) {
            summary.setAverageCompressionRatio(
                (double) summary.getTotalSpaceSaved() / summary.getTotalCompressions() * 100);
            summary.setAverageProcessingTime(
                (double) summary.getTotalProcessingTime() / summary.getTotalCompressions());
        }
        
        return summary;
    }
    
    /**
     * 重置统计信息
     */
    public void resetStats() {
        totalUploads.reset();
        totalCompressions.reset();
        totalSpaceSaved.set(0);
        totalProcessingTime.set(0);
        log.info("图片压缩统计信息已重置");
    }
    
    /**
     * 总体统计信息
     */
    public static class CompressionSummaryStats {
        private long totalUploads;
        private long totalCompressions;
        private long totalSpaceSaved;
        private long totalProcessingTime;
        private double averageCompressionRatio;
        private double averageProcessingTime;
        
        // Getters and Setters
        public long getTotalUploads() { return totalUploads; }
        public void setTotalUploads(long totalUploads) { this.totalUploads = totalUploads; }
        
        public long getTotalCompressions() { return totalCompressions; }
        public void setTotalCompressions(long totalCompressions) { this.totalCompressions = totalCompressions; }
        
        public long getTotalSpaceSaved() { return totalSpaceSaved; }
        public void setTotalSpaceSaved(long totalSpaceSaved) { this.totalSpaceSaved = totalSpaceSaved; }
        
        public long getTotalProcessingTime() { return totalProcessingTime; }
        public void setTotalProcessingTime(long totalProcessingTime) { this.totalProcessingTime = totalProcessingTime; }
        
        public double getAverageCompressionRatio() { return averageCompressionRatio; }
        public void setAverageCompressionRatio(double averageCompressionRatio) { this.averageCompressionRatio = averageCompressionRatio; }
        
        public double getAverageProcessingTime() { return averageProcessingTime; }
        public void setAverageProcessingTime(double averageProcessingTime) { this.averageProcessingTime = averageProcessingTime; }
        
        public String getFormattedTotalSpaceSaved() {
            if (totalSpaceSaved < 1024) {
                return totalSpaceSaved + " B";
            } else if (totalSpaceSaved < 1024 * 1024) {
                return String.format("%.1f KB", totalSpaceSaved / 1024.0);
            } else if (totalSpaceSaved < 1024 * 1024 * 1024) {
                return String.format("%.1f MB", totalSpaceSaved / (1024.0 * 1024.0));
            } else {
                return String.format("%.1f GB", totalSpaceSaved / (1024.0 * 1024.0 * 1024.0));
            }
        }
        
        public double getCompressionSuccessRate() {
            return totalUploads > 0 ? (double) totalCompressions / totalUploads * 100 : 0;
        }
    }
}
