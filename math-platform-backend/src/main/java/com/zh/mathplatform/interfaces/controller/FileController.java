package com.zh.mathplatform.interfaces.controller;

import com.zh.mathplatform.infrastructure.common.BaseResponse;
import com.zh.mathplatform.infrastructure.common.ResultUtils;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.manager.upload.FilePictureUpload;
import com.zh.mathplatform.infrastructure.manager.upload.UrlPictureUpload;
import com.zh.mathplatform.infrastructure.manager.upload.model.dto.file.UploadPictureResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传接口
 * 说明：当前为本地存储实现，生产建议接入对象存储（COS/OSS）
 */
@RestController
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private static final String UPLOAD_DIR = "uploads";

    /**
     * 单/多文件上传
     * 返回可直接访问的文件 URL 列表（由静态资源映射提供）
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<List<String>> upload(@RequestPart("file") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未选择文件");
        }

        ensureUploadDir();

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            // 简单类型校验（仅演示，可按需扩展白名单）
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持图片上传");
            }

            String ext = getFileExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : ("." + ext));
            Path target = Paths.get(UPLOAD_DIR, filename);
            try {
                file.transferTo(target.toFile());
            } catch (IOException e) {
                log.error("文件保存失败", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件保存失败");
            }
            // 最终可访问 URL（结合 WebMvc 静态映射），注意 context-path=/api
            String url = "/api/uploads/" + filename;
            urls.add(url);
        }

        return ResultUtils.success(urls);
    }

    // ========== COS 上传（返回富信息） ==========
    private final FilePictureUpload filePictureUpload;
    private final UrlPictureUpload urlPictureUpload;

    @PostMapping(value = "/upload/cos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<UploadPictureResult> uploadToCos(
            @RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未选择文件");
        }
        var result = filePictureUpload.uploadPicture(file, "uploads");
        return ResultUtils.success(result);
    }

    // 头像待审上传：进入隔离路径 avatar/quarantine
    @PostMapping(value = "/upload/avatar/pending", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<UploadPictureResult> uploadAvatarPending(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未选择文件");
        }
        var result = filePictureUpload.uploadPicture(file, "avatar/quarantine");
        return ResultUtils.success(result);
    }

    /**
     * 头像隔离区上传（URL 方式）
     */
    @PostMapping(value = "/upload/avatar/pendingByUrl", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<UploadPictureResult> uploadAvatarPendingByUrl(@RequestBody com.zh.mathplatform.interfaces.dto.user.AvatarUrlUploadRequest req) {
        if (req == null || !StringUtils.hasText(req.getUrl())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片链接不能为空");
        }
        var result = urlPictureUpload.uploadPicture(req.getUrl(), "avatar/quarantine");
        return ResultUtils.success(result);
    }

    private void ensureUploadDir() {
        try {
            Path dir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建上传目录失败");
        }
    }

    private String getFileExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "";
        }
        int idx = originalFilename.lastIndexOf('.');
        if (idx == -1) {
            return "";
        }
        return originalFilename.substring(idx + 1);
    }
}


