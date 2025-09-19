package com.zh.mathplatform.application.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.zh.mathplatform.application.service.WallpaperCrawlService;
import com.zh.mathplatform.application.service.WallpaperApplicationService;
import com.zh.mathplatform.domain.wallpaper.entity.Wallpaper;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.exception.ThrowUtils;
import com.zh.mathplatform.infrastructure.manager.upload.UrlPictureUpload;
import com.zh.mathplatform.infrastructure.manager.upload.model.dto.file.UploadPictureResult;
import com.zh.mathplatform.interfaces.dto.wallpaper.WallpaperBatchCrawlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 壁纸爬取服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WallpaperCrawlServiceImpl implements WallpaperCrawlService {

    private final UrlPictureUpload urlPictureUpload;
    private final WallpaperApplicationService wallpaperApplicationService;

    @Override
    public Integer batchCrawlWallpapers(WallpaperBatchCrawlRequest request, Long loginUserId) {
        String searchText = request.getSearchText();
        Integer count = request.getCount();
        String namePrefix = request.getNamePrefix();
        String category = request.getCategory();

        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "最多30条");

        // 默认名称前缀为搜索关键词
        if (StrUtil.isBlank(namePrefix)) {
            namePrefix = searchText;
        }

        // 默认分类为"学习励志"
        if (StrUtil.isBlank(category)) {
            category = "学习励志";
        }

        // 要抓取的地址
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document;
        try {
            document = Jsoup.connect(fetchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
        } catch (IOException e) {
            log.error("获取页面失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
        }

        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isNull(div)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");
        }

        Elements imgElementList = div.select("img.mimg");
        int uploadCount = 0;

        for (Element imgElement : imgElementList) {
            String fileUrl = imgElement.attr("src");
            if (StrUtil.isBlank(fileUrl)) {
                log.info("当前链接为空，已跳过");
                continue;
            }

            // 处理图片上传地址，防止出现转义问题
            int questionMarkIndex = fileUrl.indexOf("?");
            if (questionMarkIndex > -1) {
                fileUrl = fileUrl.substring(0, questionMarkIndex);
            }

            // 跳过非http/https链接
            if (!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://")) {
                log.info("跳过非HTTP链接: {}", fileUrl);
                continue;
            }

            try {
                log.info("开始处理图片: {}", fileUrl);
                
                // 上传图片到对象存储
                UploadPictureResult uploadResult = urlPictureUpload.uploadPicture(fileUrl, "wallpaper");
                
                // 创建壁纸记录
                Wallpaper wallpaper = new Wallpaper();
                wallpaper.setName(namePrefix + (uploadCount + 1));
                wallpaper.setDescription("学习加油壁纸 - " + searchText);
                wallpaper.setUrl(uploadResult.getUrl());
                wallpaper.setThumbnailUrl(uploadResult.getUrl()); // 暂时使用原图作为缩略图
                wallpaper.setCategory(category);
                wallpaper.setTags("[\"学习\",\"励志\",\"" + searchText + "\"]");
                wallpaper.setWidth(uploadResult.getPicWidth());
                wallpaper.setHeight(uploadResult.getPicHeight());
                wallpaper.setFileSize(uploadResult.getPicSize());
                wallpaper.setDownloadCount(0);
                wallpaper.setLikeCount(0);
                wallpaper.setStatus(0);
                wallpaper.setUserId(loginUserId);

                wallpaperApplicationService.saveWallpaper(wallpaper);
                log.info("壁纸创建成功, id = {}, url = {}", wallpaper.getId(), fileUrl);
                uploadCount++;

                // 防止请求过快被封禁，适当延迟
                Thread.sleep(1000);

            } catch (Exception e) {
                log.error("壁纸上传失败, url = {}, error = {}", fileUrl, e.getMessage());
                continue;
            }

            if (uploadCount >= count) {
                break;
            }
        }

        return uploadCount;
    }
}
