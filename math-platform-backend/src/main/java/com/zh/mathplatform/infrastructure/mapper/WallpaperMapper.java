package com.zh.mathplatform.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zh.mathplatform.domain.wallpaper.entity.Wallpaper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 壁纸 Mapper
 */
@Mapper
public interface WallpaperMapper extends BaseMapper<Wallpaper> {
}
