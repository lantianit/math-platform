package com.zh.mathplatform.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.domain.wallpaper.entity.Wallpaper;
import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperQueryRequest;
import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperBatchEditRequest;
import com.zh.mathplatform.interfaces.vo.wallpaper.WallpaperVO;

import java.util.List;

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

    /**
     * 按颜色搜索壁纸
     *
     * @param picColor 目标颜色（十六进制格式，如 0xFF0000 或 #FF0000）
     * @param limit 返回数量限制（默认12张）
     * @return 相似度最高的壁纸列表
     */
    List<WallpaperVO> searchWallpaperByColor(String picColor, Integer limit);

    /**
     * 批量编辑壁纸
     *
     * @param batchEditRequest 批量编辑请求
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean batchEditWallpapers(WallpaperBatchEditRequest batchEditRequest, Long userId);
}
