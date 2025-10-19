package com.zh.mathplatform.interfaces.vo.wallpaper;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 壁纸视图对象
 */
@Data
@ApiModel("WallpaperVO")
public class WallpaperVO {

    /**
     * 主键
     */
    @ApiModelProperty("Wallpaper ID")
    private Long id;

    /**
     * 壁纸名称
     */
    @ApiModelProperty("Wallpaper name")
    private String name;

    /**
     * 壁纸描述
     */
    @ApiModelProperty("Description")
    private String description;

    /**
     * 壁纸URL
     */
    @ApiModelProperty("Wallpaper URL")
    private String url;

    /**
     * 壁纸缩略图URL
     */
    @ApiModelProperty("Thumbnail URL")
    private String thumbnailUrl;

    /**
     * 壁纸分类
     */
    @ApiModelProperty("Category")
    private String category;

    /**
     * 壁纸标签列表
     */
    @ApiModelProperty("Tags")
    private List<String> tags;

    /**
     * 图片宽度
     */
    @ApiModelProperty("Width")
    private Integer width;

    /**
     * 图片高度
     */
    @ApiModelProperty("Height")
    private Integer height;

    /**
     * 文件大小（字节）
     */
    @ApiModelProperty("File size")
    private Long fileSize;

    /**
     * 图片主色调
     */
    @ApiModelProperty("Picture color")
    private String picColor;

    /**
     * 下载次数
     */
    @ApiModelProperty("Download count")
    private Integer downloadCount;

    /**
     * 点赞数
     */
    @ApiModelProperty("Like count")
    private Integer likeCount;

    /**
     * 状态
     */
    @ApiModelProperty("Status")
    private Integer status;

    /**
     * 创建用户ID
     */
    @ApiModelProperty("User ID")
    private Long userId;

    /**
     * 创建时间
     */
    @ApiModelProperty("Create time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
