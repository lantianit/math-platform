package com.zh.mathplatform.infrastructure.api;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.CopyObjectRequest;
import com.qcloud.cos.model.CopyObjectResult;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.zh.mathplatform.infrastructure.config.CosClientConfig;
import com.zh.mathplatform.infrastructure.config.PictureCompressionConfig;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static cn.dev33.satoken.SaManager.log;

@Component
@Slf4j
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    @Resource
    private PictureCompressionConfig compressionConfig;

    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传图片并进行压缩处理
     * 1. 将图片转换为WebP格式以减小文件大小
     * 2. 对于大图片生成缩略图
     * 3. 进行质量压缩优化
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        
        // 如果未启用压缩，直接上传
        if (!compressionConfig.isEnabled()) {
            return cosClient.putObject(putObjectRequest);
        }
        
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        List<PicOperations.Rule> rules = new ArrayList<>();
        
        // 规则1: 图片压缩（转成目标格式，保持原始尺寸）
        String compressedKey = FileUtil.mainName(key) + compressionConfig.getCompressionExtension();
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setFileId(compressedKey);
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setRule(compressionConfig.getCompressionRule());
        rules.add(compressRule);
        
        // 规则2: 缩略图生成（缩小尺寸用于列表预览）
        // 仅对大于设定大小（默认 2KB）的图片生成缩略图
        // 避免小图片生成缩略图反而更大的情况
        if (file.length() > compressionConfig.getThumbnailMinFileSize()) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail" + compressionConfig.getThumbnailExtension();
            thumbnailRule.setFileId(thumbnailKey);
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            // 缩略图规则：thumbnail/128x128>（大于原图时不处理）+ format/webp + quality/75
            thumbnailRule.setRule(compressionConfig.getThumbnailRule());
            rules.add(thumbnailRule);
            log.debug("图片大小 {} 字节，将生成缩略图", file.length());
        } else {
            log.debug("图片大小 {} 字节，小于阈值 {}，跳过缩略图生成",
                file.length(), compressionConfig.getThumbnailMinFileSize());
        }
        
        // 构造处理参数
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }

    public void deleteObject(String key) {
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }

    public CopyObjectResult copyObject(String sourceKey, String targetKey) {
        String bucket = cosClientConfig.getBucket();
        CopyObjectRequest request = new CopyObjectRequest(bucket, sourceKey, bucket, targetKey);
        return cosClient.copyObject(request);
    }
}


