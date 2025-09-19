package com.zh.mathplatform.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.wallpaper.entity.Wallpaper;
import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperQueryRequest;
import com.zh.mathplatform.interfaces.vo.wallpaper.WallpaperVO;

/**
 * 壁纸应用服务
 */
public interface WallpaperApplicationService {

    /**
     * 保存壁纸
     *
     * @param wallpaper 壁纸实体
     * @return 是否成功
     */
    boolean saveWallpaper(Wallpaper wallpaper);

    /**
     * 分页查询壁纸
     *
     * @param queryRequest 查询请求
     * @return 壁纸分页数据
     */
    Page<WallpaperVO> pageWallpapers(WallpaperQueryRequest queryRequest);

    /**
     * 根据ID获取壁纸
     *
     * @param id 壁纸ID
     * @return 壁纸VO
     */
    WallpaperVO getWallpaperById(Long id);

    /**
     * 增加下载次数
     *
     * @param id 壁纸ID
     * @return 是否成功
     */
    boolean incrementDownloadCount(Long id);

    /**
     * 增加点赞数
     *
     * @param id 壁纸ID
     * @return 是否成功
     */
    boolean incrementLikeCount(Long id);

    /**
     * 删除壁纸
     *
     * @param id 壁纸ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteWallpaper(Long id, Long userId);
}
