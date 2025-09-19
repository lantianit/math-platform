package com.zh.mathplatform.infrastructure.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * 通过远程 URL 上传图片（下载到本地临时文件后再走统一的 COS 处理流程）
 */
@Service
@Slf4j
public class UrlPictureUpload extends PictureUploadTemplate {

    private static final long MAX_SIZE_BYTES = 2L * 1024 * 1024; // 2MB，与本地上传保持一致
    private static final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "png", "jpg", "webp");

    @Override
    protected void validPicture(Object inputSource) {
        String url = (String) inputSource;
        ThrowUtils.throwIf(StrUtil.isBlank(url), ErrorCode.PARAMS_ERROR, "图片链接不能为空");
        // 基础 URL 校验
        try {
            URL u = new URL(url);
            String protocol = u.getProtocol();
            ThrowUtils.throwIf(!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol),
                    ErrorCode.PARAMS_ERROR, "仅支持 http/https 链接");
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片链接非法");
        }

        // 预检 HEAD：类型与大小（部分站点可能不返回 Content-Length，此时放行到下载阶段二次校验）
        try (HttpResponse head = HttpRequest.head(url)
                .timeout(5000)
                .execute()) {
            if (head != null && head.isOk()) {
                String contentType = head.header("Content-Type");
                ThrowUtils.throwIf(StrUtil.isBlank(contentType) || !contentType.toLowerCase().startsWith("image/"),
                        ErrorCode.PARAMS_ERROR, "仅支持图片链接");

                String len = head.header("Content-Length");
                if (StrUtil.isNotBlank(len)) {
                    try {
                        long size = Long.parseLong(len);
                        ThrowUtils.throwIf(size > MAX_SIZE_BYTES, ErrorCode.PARAMS_ERROR, "图片大小不能超过 2MB");
                    } catch (NumberFormatException ignore) {
                        // 忽略，进入下载阶段校验实际大小
                    }
                }
            }
        }
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        String url = (String) inputSource;
        // 从 URL 推断文件名与后缀
        String name = StrUtil.blankToDefault(FileUtil.getName(url), "image");
        String suffix = FileUtil.getSuffix(name);
        if (StrUtil.isBlank(suffix)) {
            // 默认 jpg 后缀，后续仍会基于 COS 处理得到真实格式
            name = name + ".jpg";
        }
        return name;
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        String url = (String) inputSource;
        // 实际下载到临时文件
        HttpUtil.downloadFile(url, file);
        // 大小校验
        long size = FileUtil.size(file);
        ThrowUtils.throwIf(size > MAX_SIZE_BYTES, ErrorCode.PARAMS_ERROR, "图片大小不能超过 2MB");

        // 后缀与类型白名单校验（基于文件名后缀）
        String suffix = FileUtil.getSuffix(getOriginFilename(inputSource));
        // 对于网络抓取的图片，放宽文件类型限制
        if (StrUtil.isBlank(suffix)) {
            // 如果无法从URL获取后缀，默认为jpg
            log.info("无法从URL获取文件后缀，默认为jpg: {}", url);
        } else if (!ALLOW_FORMAT_LIST.contains(suffix.toLowerCase())) {
            // 如果后缀不在白名单中，记录日志但允许通过
            log.warn("文件后缀不在白名单中，但允许通过: {}", suffix);
        }
    }

}


