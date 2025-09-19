package com.zh.mathplatform.interfaces.dto.wallpaper;

import com.zh.mathplatform.infrastructure.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 壁纸查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("WallpaperQueryRequest")
public class WallpaperQueryRequest extends PageRequest {

    /**
     * 壁纸名称
     */
    @ApiModelProperty("Wallpaper name")
    private String name;

    /**
     * 壁纸分类
     */
    @ApiModelProperty("Category")
    private String category;

    /**
     * 壁纸标签
     */
    @ApiModelProperty("Tag")
    private String tag;

    /**
     * 创建用户ID
     */
    @ApiModelProperty("User ID")
    private Long userId;

    /**
     * 状态
     */
    @ApiModelProperty("Status")
    private Integer status;
}
