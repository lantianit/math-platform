package com.zh.mathplatform.application.service;

import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperBatchCrawlRequest;

/**
 * 壁纸爬取服务
 */
public interface WallpaperCrawlService {

    /**
     * 批量爬取并创建壁纸
     *
     * @param request   爬取请求
     * @param loginUser 当前登录用户ID
     * @return 成功创建的壁纸数量
     */
    Integer batchCrawlWallpapers(WallpaperBatchCrawlRequest request, Long loginUser);
}
