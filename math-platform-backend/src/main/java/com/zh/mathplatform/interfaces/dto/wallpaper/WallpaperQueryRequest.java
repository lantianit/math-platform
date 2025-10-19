package com.zh.mathplatform.interfaces.dto.wallpaper;

import com.zh.mathplatform.infrastructure.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 壁纸查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("WallpaperQueryRequest")
public class WallpaperQueryRequest extends PageRequest {

    /**
     * 关键词（同时搜索名称和简介）
     */
    @ApiModelProperty("Search keyword")
    private String searchText;

    /**
     * 壁纸名称
     */
    @ApiModelProperty("Wallpaper name")
    private String name;

    /**
     * 壁纸简介
     */
    @ApiModelProperty("Introduction/Description")
    private String introduction;

    /**
     * 壁纸分类
     */
    @ApiModelProperty("Category")
    private String category;

    /**
     * 壁纸标签（单个）
     */
    @ApiModelProperty("Tag")
    private String tag;

    /**
     * 壁纸标签列表（多个）
     */
    @ApiModelProperty("Tags list")
    private List<String> tags;

    /**
     * 图片宽度
     */
    @ApiModelProperty("Picture width")
    private Integer picWidth;

    /**
     * 图片高度
     */
    @ApiModelProperty("Picture height")
    private Integer picHeight;

    /**
     * 图片格式
     */
    @ApiModelProperty("Picture format")
    private String picFormat;

    /**
     * 开始编辑时间
     */
    @ApiModelProperty("Start edit time")
    private Date startEditTime;

    /**
     * 结束编辑时间
     */
    @ApiModelProperty("End edit time")
    private Date endEditTime;

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
