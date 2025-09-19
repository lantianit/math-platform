package com.zh.mathplatform.application.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.WallpaperApplicationService;
import com.zh.mathplatform.domain.wallpaper.entity.Wallpaper;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.exception.ThrowUtils;
import com.zh.mathplatform.infrastructure.mapper.WallpaperMapper;
import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperQueryRequest;
import com.zh.mathplatform.interfaces.vo.wallpaper.WallpaperVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 壁纸应用服务实现
 */
@Service
@RequiredArgsConstructor
public class WallpaperApplicationServiceImpl implements WallpaperApplicationService {

    private final WallpaperMapper wallpaperMapper;

    @Override
    public boolean saveWallpaper(Wallpaper wallpaper) {
        ThrowUtils.throwIf(wallpaper == null, ErrorCode.PARAMS_ERROR);
        return wallpaperMapper.insert(wallpaper) > 0;
    }

    @Override
    public Page<WallpaperVO> pageWallpapers(WallpaperQueryRequest queryRequest) {
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();

        // 构建查询条件
        QueryWrapper<Wallpaper> queryWrapper = new QueryWrapper<>();
        
        String name = queryRequest.getName();
        String category = queryRequest.getCategory();
        String tag = queryRequest.getTag();
        Long userId = queryRequest.getUserId();
        Integer status = queryRequest.getStatus();

        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.like(StrUtil.isNotBlank(tag), "tags", tag);
        queryWrapper.eq(userId != null, "user_id", userId);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.orderByDesc("create_time");

        // 分页查询
        Page<Wallpaper> wallpaperPage = wallpaperMapper.selectPage(new Page<>(current, size), queryWrapper);
        
        // 转换为VO
        Page<WallpaperVO> wallpaperVOPage = new Page<>(current, size, wallpaperPage.getTotal());
        List<WallpaperVO> wallpaperVOList = wallpaperPage.getRecords().stream()
                .map(this::convertToVO)
                .toList();
        wallpaperVOPage.setRecords(wallpaperVOList);

        return wallpaperVOPage;
    }

    @Override
    public WallpaperVO getWallpaperById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        Wallpaper wallpaper = wallpaperMapper.selectById(id);
        ThrowUtils.throwIf(wallpaper == null, ErrorCode.NOT_FOUND_ERROR);
        
        return convertToVO(wallpaper);
    }

    @Override
    public boolean incrementDownloadCount(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        Wallpaper wallpaper = wallpaperMapper.selectById(id);
        ThrowUtils.throwIf(wallpaper == null, ErrorCode.NOT_FOUND_ERROR);
        
        wallpaper.setDownloadCount((wallpaper.getDownloadCount() == null ? 0 : wallpaper.getDownloadCount()) + 1);
        return wallpaperMapper.updateById(wallpaper) > 0;
    }

    @Override
    public boolean incrementLikeCount(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        Wallpaper wallpaper = wallpaperMapper.selectById(id);
        ThrowUtils.throwIf(wallpaper == null, ErrorCode.NOT_FOUND_ERROR);
        
        wallpaper.setLikeCount((wallpaper.getLikeCount() == null ? 0 : wallpaper.getLikeCount()) + 1);
        return wallpaperMapper.updateById(wallpaper) > 0;
    }

    @Override
    public boolean deleteWallpaper(Long id, Long userId) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR);
        
        Wallpaper wallpaper = wallpaperMapper.selectById(id);
        ThrowUtils.throwIf(wallpaper == null, ErrorCode.NOT_FOUND_ERROR);
        
        // 只有创建者或管理员可以删除
        // 这里简化处理，实际应该检查用户权限
        if (wallpaper.getUserId() == null || !wallpaper.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        
        return wallpaperMapper.deleteById(id) > 0;
    }

    /**
     * 实体转VO
     */
    private WallpaperVO convertToVO(Wallpaper wallpaper) {
        if (wallpaper == null) {
            return null;
        }
        
        WallpaperVO wallpaperVO = new WallpaperVO();
        BeanUtils.copyProperties(wallpaper, wallpaperVO);
        
        // 解析标签JSON
        if (StrUtil.isNotBlank(wallpaper.getTags())) {
            try {
                List<String> tags = JSONUtil.toList(wallpaper.getTags(), String.class);
                wallpaperVO.setTags(tags);
            } catch (Exception e) {
                // JSON解析失败，设为空列表
                wallpaperVO.setTags(List.of());
            }
        } else {
            wallpaperVO.setTags(List.of());
        }
        
        return wallpaperVO;
    }
}
