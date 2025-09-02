package com.zh.mathplatform.application.service;

import java.util.Map;

/**
 * 统计应用服务接口
 */
public interface StatisticsApplicationService {

    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStatistics(Long userId);

    /**
     * 获取帖子统计信息
     */
    Map<String, Object> getPostStatistics(Long postId);

    /**
     * 获取平台总体统计信息
     */
    Map<String, Object> getPlatformStatistics();

    /**
     * 获取用户活跃度统计
     */
    Map<String, Object> getUserActivityStatistics(Long userId, Integer days);

    /**
     * 获取热门内容统计
     */
    Map<String, Object> getHotContentStatistics(Integer days);

    /**
     * 计算帖子热度评分
     */
    Double calculatePostHotScore(Long postId);

    /**
     * 计算帖子质量评分
     */
    Double calculatePostQualityScore(Long postId);

    /**
     * 更新用户统计信息
     */
    boolean updateUserStatistics(Long userId);

    /**
     * 更新帖子统计信息
     */
    boolean updatePostStatistics(Long postId);
}
