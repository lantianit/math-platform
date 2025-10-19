package com.zh.mathplatform.interfaces.dto.wallpaper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 按颜色搜索壁纸请求
 */
@Data
@ApiModel("SearchWallpaperByColorRequest")
public class SearchWallpaperByColorRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片主色调（十六进制颜色值，如 0xFF0000、#FF0000）
     */
    @ApiModelProperty(value = "目标颜色（十六进制）", example = "#FF0000")
    private String picColor;

    /**
     * 返回数量限制
     */
    @ApiModelProperty(value = "返回数量", example = "12")
    private Integer limit;
}

