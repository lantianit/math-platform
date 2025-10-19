package com.zh.mathplatform.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zh.mathplatform.application.service.WallpaperApplicationService;
import com.zh.mathplatform.application.service.WallpaperCrawlService;
import com.zh.mathplatform.domain.user.constant.UserConstant;
import com.zh.mathplatform.infrastructure.annotation.AuthCheck;
import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.DeleteRequest;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.exception.ThrowUtils;
import com.zh.mathplatform.infrastructure.utils.UserHolder;

import javax.servlet.http.HttpServletRequest;
import com.zh.mathplatform.infrastructure.api.imagesearch.ImageSearchApiFacade;
import com.zh.mathplatform.infrastructure.api.imagesearch.model.ImageSearchResult;
import com.zh.mathplatform.interfaces.dto.wallpaper.SearchPictureByPictureRequest;
import com.zh.mathplatform.interfaces.dto.wallpaper.SearchWallpaperByColorRequest;
import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperBatchCrawlRequest;
import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperQueryRequest;
import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperBatchEditRequest;
import com.zh.mathplatform.interfaces.vo.wallpaper.WallpaperVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 壁纸接口
 */
@RestController
@RequestMapping("/wallpaper")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "wallpaperController")
public class WallpaperController {

    private final WallpaperApplicationService wallpaperApplicationService;
    private final WallpaperCrawlService wallpaperCrawlService;

    /**
     * 分页获取壁纸列表
     */
    @PostMapping("/list/page")
    @ApiOperation("分页获取壁纸列表")
    public BaseResponse<Page<WallpaperVO>> listWallpapersByPage(@RequestBody WallpaperQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        
        // 默认只查询正常状态的壁纸
        if (queryRequest.getStatus() == null) {
            queryRequest.setStatus(0);
        }
        
        Page<WallpaperVO> wallpaperPage = wallpaperApplicationService.pageWallpapers(queryRequest);
        return ResultUtils.success(wallpaperPage);
    }

    /**
     * 根据ID获取壁纸
     */
    @GetMapping("/get/{id}")
    @ApiOperation("根据ID获取壁纸")
    public BaseResponse<WallpaperVO> getWallpaperById(@PathVariable Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        WallpaperVO wallpaperVO = wallpaperApplicationService.getWallpaperById(id);
        return ResultUtils.success(wallpaperVO);
    }

    /**
     * 增加下载次数
     */
    @PostMapping("/download/{id}")
    @ApiOperation("增加下载次数")
    public BaseResponse<Boolean> incrementDownloadCount(@PathVariable Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        boolean result = wallpaperApplicationService.incrementDownloadCount(id);
        return ResultUtils.success(result);
    }

    /**
     * 点赞壁纸
     */
    @PostMapping("/like/{id}")
    @ApiOperation("点赞壁纸")
    public BaseResponse<Boolean> likeWallpaper(@PathVariable Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        
        boolean result = wallpaperApplicationService.incrementLikeCount(id);
        return ResultUtils.success(result);
    }

    /**
     * 删除壁纸（仅管理员或创建者）
     */
    @PostMapping("/delete")
    @ApiOperation("删除壁纸")
    public BaseResponse<Boolean> deleteWallpaper(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        
        Long userId = UserHolder.getLoginUserIdRequired(request);
        boolean result = wallpaperApplicationService.deleteWallpaper(deleteRequest.getId(), userId);
        return ResultUtils.success(result);
    }

    // ========== 管理员功能 ==========

    /**
     * 批量爬取并创建壁纸（仅管理员）
     */
    @PostMapping("/batch/crawl")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation("批量爬取并创建壁纸")
    public BaseResponse<Integer> batchCrawlWallpapers(@Valid @RequestBody WallpaperBatchCrawlRequest request, HttpServletRequest httpRequest) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        
        Long userId = UserHolder.getLoginUserIdRequired(httpRequest);
        Integer count = wallpaperCrawlService.batchCrawlWallpapers(request, userId);
        return ResultUtils.success(count);
    }

    /**
     * 分页获取所有壁纸（包括隐藏的，仅管理员）
     */
    @PostMapping("/admin/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @ApiOperation("分页获取所有壁纸")
    public BaseResponse<Page<WallpaperVO>> listAllWallpapersByPage(@RequestBody WallpaperQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        
        Page<WallpaperVO> wallpaperPage = wallpaperApplicationService.pageWallpapers(queryRequest);
        return ResultUtils.success(wallpaperPage);
    }

    /**
     * 以图搜图
     */
    @PostMapping("/search/picture")
    @ApiOperation("以图搜图")
    public BaseResponse<List<ImageSearchResult>> searchPictureByPicture(@RequestBody SearchPictureByPictureRequest searchPictureByPictureRequest) {
        ThrowUtils.throwIf(searchPictureByPictureRequest == null, ErrorCode.PARAMS_ERROR);
        Long pictureId = searchPictureByPictureRequest.getPictureId();
        ThrowUtils.throwIf(pictureId == null || pictureId <= 0, ErrorCode.PARAMS_ERROR);
        
        WallpaperVO wallpaperVO = wallpaperApplicationService.getWallpaperById(pictureId);
        ThrowUtils.throwIf(wallpaperVO == null, ErrorCode.NOT_FOUND_ERROR);
        
        List<ImageSearchResult> resultList = ImageSearchApiFacade.searchImage(wallpaperVO.getUrl());
        return ResultUtils.success(resultList);
    }

    /**
     * 按颜色搜索壁纸
     */
    @PostMapping("/search/color")
    @ApiOperation("按颜色搜索壁纸")
    public BaseResponse<List<WallpaperVO>> searchWallpaperByColor(
            @RequestBody SearchWallpaperByColorRequest searchWallpaperByColorRequest) {
        ThrowUtils.throwIf(searchWallpaperByColorRequest == null, ErrorCode.PARAMS_ERROR);
        
        String picColor = searchWallpaperByColorRequest.getPicColor();
        Integer limit = searchWallpaperByColorRequest.getLimit();
        
        List<WallpaperVO> result = wallpaperApplicationService.searchWallpaperByColor(picColor, limit);
        return ResultUtils.success(result);
    }

    /**
     * 批量编辑壁纸
     */
    @PostMapping("/batch/edit")
    @ApiOperation("批量编辑壁纸")
    public BaseResponse<Boolean> batchEditWallpapers(@RequestBody WallpaperBatchEditRequest batchEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(batchEditRequest == null, ErrorCode.PARAMS_ERROR);
        
        Long userId = UserHolder.getLoginUserIdRequired(request);
        boolean result = wallpaperApplicationService.batchEditWallpapers(batchEditRequest, userId);
        return ResultUtils.success(result);
    }
}
