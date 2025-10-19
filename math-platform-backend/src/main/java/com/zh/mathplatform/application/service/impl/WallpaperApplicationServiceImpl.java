package com.zh.mathplatform.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.WallpaperApplicationService;
import com.zh.mathplatform.domain.wallpaper.entity.Wallpaper;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.exception.ThrowUtils;
import com.zh.mathplatform.infrastructure.api.CosManager;
import com.zh.mathplatform.infrastructure.mapper.WallpaperMapper;
import com.zh.mathplatform.infrastructure.utils.ColorSimilarUtils;
import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperQueryRequest;
import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperBatchEditRequest;
import com.zh.mathplatform.interfaces.vo.wallpaper.WallpaperVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 壁纸应用服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WallpaperApplicationServiceImpl implements WallpaperApplicationService {

    private final WallpaperMapper wallpaperMapper;
    private final CosManager cosManager;

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
        
        // 提取查询参数
        String searchText = queryRequest.getSearchText();
        String name = queryRequest.getName();
        String introduction = queryRequest.getIntroduction();
        String category = queryRequest.getCategory();
        String tag = queryRequest.getTag();
        Long userId = queryRequest.getUserId();
        Integer status = queryRequest.getStatus();
        Integer picWidth = queryRequest.getPicWidth();
        Integer picHeight = queryRequest.getPicHeight();
        String picFormat = queryRequest.getPicFormat();
        java.util.Date startEditTime = queryRequest.getStartEditTime();
        java.util.Date endEditTime = queryRequest.getEndEditTime();
        java.util.List<String> tags = queryRequest.getTags();

        // 关键词搜索（同时搜索名称和简介）
        if (StrUtil.isNotBlank(searchText)) {
            queryWrapper.and(wrapper -> wrapper
                    .like("name", searchText)
                    .or()
                    .like("description", searchText)
            );
        }

        // 精确字段搜索
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "description", introduction);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.like(StrUtil.isNotBlank(tag), "tags", tag);
        
        // 标签列表搜索（匹配任意一个标签）
        if (tags != null && !tags.isEmpty()) {
            queryWrapper.and(wrapper -> {
                for (int i = 0; i < tags.size(); i++) {
                    if (i > 0) {
                        wrapper.or();
                    }
                    wrapper.like("tags", tags.get(i));
                }
            });
        }
        
        // 图片尺寸搜索
        queryWrapper.eq(picWidth != null, "width", picWidth);
        queryWrapper.eq(picHeight != null, "height", picHeight);
        
        // 图片格式搜索（从URL中提取，可能需要特殊处理）
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "url", "." + picFormat);
        
        // 编辑时间范围搜索
        queryWrapper.ge(startEditTime != null, "update_time", startEditTime);
        queryWrapper.lt(endEditTime != null, "update_time", endEditTime);
        
        // 其他条件
        queryWrapper.eq(userId != null, "user_id", userId);
        queryWrapper.eq(status != null, "status", status);
        
        // 排序
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
        
        try {
            // 1. 数据库逻辑删除
            boolean dbResult = wallpaperMapper.deleteById(id) > 0;
            
            // 2. 删除COS中的文件
            if (dbResult && StrUtil.isNotBlank(wallpaper.getUrl())) {
                try {
                    // 从URL中提取objectKey
                    String objectKey = extractObjectKeyFromUrl(wallpaper.getUrl());
                    if (StrUtil.isNotBlank(objectKey)) {
                        cosManager.deleteObject(objectKey);
                        log.info("成功删除COS文件: {}", objectKey);
                    }
                    
                    // 删除缩略图
                    if (StrUtil.isNotBlank(wallpaper.getThumbnailUrl())) {
                        String thumbnailKey = extractObjectKeyFromUrl(wallpaper.getThumbnailUrl());
                        if (StrUtil.isNotBlank(thumbnailKey)) {
                            cosManager.deleteObject(thumbnailKey);
                            log.info("成功删除COS缩略图: {}", thumbnailKey);
                        }
                    }
                } catch (Exception e) {
                    log.error("删除COS文件失败，但数据库已删除: {}", e.getMessage());
                    // 即使COS删除失败，数据库删除已成功，仍然返回true
                }
            }
            
            return dbResult;
        } catch (Exception e) {
            log.error("删除壁纸失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
    }

    @Override
    public List<WallpaperVO> searchWallpaperByColor(String picColor, Integer limit) {
        // 1. 参数校验
        ThrowUtils.throwIf(StrUtil.isBlank(picColor), ErrorCode.PARAMS_ERROR, "颜色参数不能为空");
        
        // 默认返回12张，最小1张，最大100张
        if (limit == null || limit <= 0) {
            limit = 12;
        } else if (limit > 100) {
            limit = 100;  // 限制最大100张，避免性能问题
        }
        
        // 2. 查询所有有主色调的正常状态壁纸
        QueryWrapper<Wallpaper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0)  // 只查询正常状态的壁纸
                .isNotNull("pic_color")
                .ne("pic_color", "");  // 有主色调的壁纸
        
        List<Wallpaper> wallpaperList = wallpaperMapper.selectList(queryWrapper);
        
        // 如果没有壁纸，直接返回空列表
        if (CollUtil.isEmpty(wallpaperList)) {
            return Collections.emptyList();
        }
        
        // 3. 标准化目标颜色
        String normalizedTargetColor;
        try {
            normalizedTargetColor = ColorSimilarUtils.normalizeHexColor(picColor);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的颜色格式: " + picColor);
        }
        
        Color targetColor = Color.decode(normalizedTargetColor);
        
        // 4. 计算相似度并排序
        final int finalLimit = limit;
        List<Wallpaper> sortedWallpapers = wallpaperList.stream()
                .filter(wallpaper -> StrUtil.isNotBlank(wallpaper.getPicColor()))
                .sorted(Comparator.comparingDouble(wallpaper -> {
                    try {
                        String hexColor = wallpaper.getPicColor();
                        Color pictureColor = Color.decode(ColorSimilarUtils.normalizeHexColor(hexColor));
                        // 返回负值，因为相似度越大越好（降序排列）
                        return -ColorSimilarUtils.calculateSimilarity(targetColor, pictureColor);
                    } catch (Exception e) {
                        // 颜色解析失败的放到最后
                        return Double.MAX_VALUE;
                    }
                }))
                .limit(finalLimit)
                .collect(Collectors.toList());
        
        // 5. 转换为 VO
        return sortedWallpapers.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
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
    
    /**
     * 从URL中提取COS的objectKey
     * 例如: https://bucket.cos.region.myqcloud.com/path/file.jpg -> path/file.jpg
     */
    private String extractObjectKeyFromUrl(String url) {
        if (StrUtil.isBlank(url)) {
            return null;
        }
        
        try {
            // 移除协议和域名部分，获取路径
            // 例如: https://bucket.cos.region.myqcloud.com/path/file.jpg
            // 提取: path/file.jpg
            int lastSlashIndex = url.lastIndexOf('/');
            if (lastSlashIndex > 0) {
                // 找到最后一个/之前的部分，再找倒数第二个/
                String beforeLastSlash = url.substring(0, lastSlashIndex);
                int secondLastSlashIndex = beforeLastSlash.lastIndexOf('/');
                if (secondLastSlashIndex > 0) {
                    return url.substring(secondLastSlashIndex + 1);
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("解析URL获取objectKey失败: {}", url, e);
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchEditWallpapers(WallpaperBatchEditRequest batchEditRequest, Long userId) {
        List<Long> wallpaperIdList = batchEditRequest.getWallpaperIdList();
        String category = batchEditRequest.getCategory();
        List<String> tags = batchEditRequest.getTags();
        String nameRule = batchEditRequest.getNameRule();

        // 1. 校验参数
        ThrowUtils.throwIf(CollUtil.isEmpty(wallpaperIdList), ErrorCode.PARAMS_ERROR, "壁纸ID列表不能为空");
        ThrowUtils.throwIf(userId == null, ErrorCode.NO_AUTH_ERROR, "用户未登录");

        // 2. 查询指定壁纸，仅选择需要的字段
        QueryWrapper<Wallpaper> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", wallpaperIdList);
        List<Wallpaper> wallpaperList = wallpaperMapper.selectList(queryWrapper);
        if (wallpaperList.isEmpty()) {
            return true; // 没有需要更新的壁纸
        }

        // 3. 更新分类、标签和名称
        wallpaperList.forEach(wallpaper -> {
            if (StrUtil.isNotBlank(category)) {
                wallpaper.setCategory(category);
            }
            if (CollUtil.isNotEmpty(tags)) {
                wallpaper.setTags(JSONUtil.toJsonStr(tags));
            }
        });

        // 4. 批量重命名
        if (StrUtil.isNotBlank(nameRule)) {
            fillWallpaperWithNameRule(wallpaperList, nameRule);
        }

        // 5. 批量更新
        for (Wallpaper wallpaper : wallpaperList) {
            wallpaperMapper.updateById(wallpaper);
        }
        
        return true;
    }

    /**
     * 根据命名规则填充壁纸名称
     * nameRule 格式：壁纸{序号}
     *
     * @param wallpaperList 壁纸列表
     * @param nameRule 命名规则
     */
    private void fillWallpaperWithNameRule(List<Wallpaper> wallpaperList, String nameRule) {
        if (CollUtil.isEmpty(wallpaperList) || StrUtil.isBlank(nameRule)) {
            return;
        }
        long count = 1;
        try {
            for (Wallpaper wallpaper : wallpaperList) {
                String wallpaperName = nameRule.replaceAll("\\{序号}", String.valueOf(count++));
                wallpaper.setName(wallpaperName);
            }
        } catch (Exception e) {
            log.error("名称解析错误", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "名称解析错误");
        }
    }
}
