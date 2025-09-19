package com.zh.mathplatform.interfaces.dto.wallpaper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 壁纸批量抓取请求
 */
@Data
@ApiModel("WallpaperBatchCrawlRequest")
public class WallpaperBatchCrawlRequest {

    /**
     * 搜索关键词
     */
    @ApiModelProperty(value = "Search text", required = true, example = "study motivation")
    @NotBlank(message = "搜索关键词不能为空")
    private String searchText;

    /**
     * 抓取数量
     */
    @ApiModelProperty(value = "Crawl count", example = "10")
    @Min(value = 1, message = "抓取数量至少为1")
    @Max(value = 30, message = "抓取数量最多为30")
    private Integer count = 10;

    /**
     * 名称前缀
     */
    @ApiModelProperty(value = "Name prefix")
    private String namePrefix;

    /**
     * 壁纸分类
     */
    @ApiModelProperty(value = "Category")
    private String category;
}
