package com.zh.mathplatform.infrastructure.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import com.zh.mathplatform.infrastructure.api.CosManager;
import com.zh.mathplatform.infrastructure.config.CosClientConfig;
import com.zh.mathplatform.infrastructure.exception.BusinessException;
import com.zh.mathplatform.infrastructure.exception.ErrorCode;
import com.zh.mathplatform.infrastructure.manager.upload.model.dto.file.PictureCompressionStats;
import com.zh.mathplatform.infrastructure.manager.upload.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    @Resource
    private PictureCompressionStatsManager statsManager;

    /**
     * 上传图片并进行压缩处理
     * 支持自动WebP格式转换和缩略图生成
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        validPicture(inputSource);
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginFilename(inputSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        long startTime = System.currentTimeMillis();
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            // 处理文件来源（本地或 URL）
            processFile(inputSource, file);
            
            // 记录原始文件信息
            long originalFileSize = file.length();
            String originalFormat = FileUtil.getSuffix(originalFilename);
            
            // 上传图片到对象存储
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            if (CollUtil.isNotEmpty(objectList)) {
                CIObject compressedCiObject = objectList.get(0);
                CIObject thumbnailCiObject = objectList.size() > 1 ? objectList.get(1) : compressedCiObject;
                
                // 创建压缩统计信息
                PictureCompressionStats stats = new PictureCompressionStats();
                stats.setOriginalSize(originalFileSize);
                stats.setCompressedSize(compressedCiObject.getSize().longValue());
                stats.setOriginalFormat(originalFormat);
                stats.setCompressedFormat(compressedCiObject.getFormat());
                stats.setHasThumbnail(objectList.size() > 1);
                stats.setProcessingTime(processingTime);
                if (stats.isHasThumbnail() && objectList.size() > 1) {
                    stats.setThumbnailSize(objectList.get(1).getSize().longValue());
                }
                stats.calculateCompressionRatio();
                
                // 封装压缩图返回结果
                UploadPictureResult r = buildResult(originalFilename, compressedCiObject, thumbnailCiObject, imageInfo);
                r.setEtag(putObjectResult.getETag());
                r.setCompressionStats(stats);
                
                // 记录统计信息
                statsManager.recordCompressionStats(stats);
                
                log.info("图片上传成功，已压缩为{}格式，{} -> {}，压缩率: {}，处理耗时: {}ms", 
                    stats.getCompressedFormat(),
                    stats.getFormattedOriginalSize(),
                    stats.getFormattedCompressedSize(),
                    stats.getFormattedCompressionRatio(),
                    processingTime);
                return r;
            }
            
            // 封装原图返回结果（未压缩情况）
            UploadPictureResult r = buildResult(originalFilename, file, uploadPath, imageInfo);
            r.setEtag(putObjectResult.getETag());
            log.warn("图片未进行压缩处理，使用原图: {}", uploadPath);
            return r;
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }

    protected abstract void validPicture(Object inputSource);

    protected abstract String getOriginFilename(Object inputSource);

    protected abstract void processFile(Object inputSource, File file) throws Exception;

    /**
     * 构建压缩图片的返回结果
     * 从压缩后的CIObject中获取图片信息
     */
    private UploadPictureResult buildResult(String originalFilename, CIObject compressedCiObject, CIObject thumbnailCiObject,
                                            ImageInfo imageInfo) {
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        // 设置图片为压缩后的地址（WebP格式）
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
        uploadPictureResult.setObjectKey(compressedCiObject.getKey());
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue());
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat()); // 应该是 "webp"
        uploadPictureResult.setPicColor(imageInfo.getAve());
        
        // 设置缩略图信息
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        uploadPictureResult.setThumbnailKey(thumbnailCiObject.getKey());
        
        return uploadPictureResult;
    }

    private UploadPictureResult buildResult(String originalFilename, File file, String uploadPath, ImageInfo imageInfo) {
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        uploadPictureResult.setObjectKey(uploadPath);
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicColor(imageInfo.getAve());
        return uploadPictureResult;
    }

    public void deleteTempFile(File file) {
        if (file == null) return;
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}


