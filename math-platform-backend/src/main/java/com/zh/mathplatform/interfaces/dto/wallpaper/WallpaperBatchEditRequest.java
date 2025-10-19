package com.zh.mathplatform.interfaces.dto.wallpaper;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量编辑壁纸请求
 */
@Data
public class WallpaperBatchEditRequest implements Serializable {

    /**
     * 壁纸 id 列表
     */
    private List<Long> wallpaperIdList;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 命名规则
     */
    private String nameRule;

    private static final long serialVersionUID = 1L;
}
