package com.zh.mathplatform.infrastructure.config;

import cn.hutool.core.util.StrUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
@Slf4j
public class CosClientConfig {

    private String host;
    private String secretId;
    private String secretKey;
    private String region;
    private String bucket;

    /**
     * 配置初始化后验证
     */
    @PostConstruct
    public void init() {
        log.info("===== COS 配置信息 =====");
        log.info("Host: {}", host);
        log.info("Region: {}", region);
        log.info("Bucket: {}", bucket);
        log.info("SecretId: {}", StrUtil.isNotBlank(secretId) ? secretId.substring(0, Math.min(10, secretId.length())) + "..." : "未配置");
        log.info("=======================");
        
        // 验证必需配置
        if (StrUtil.isBlank(host)) {
            log.error("❌ COS配置错误：host 未配置！请在 application-dev.yml 中添加 cos.client.host 配置");
            log.error("正确格式：host: https://{bucket}.cos.{region}.myqcloud.com");
            log.error("示例：host: https://yupi-1318253552.cos.ap-guangzhou.myqcloud.com");
            throw new IllegalStateException("COS配置错误：host 不能为空");
        }
        
        if (StrUtil.isBlank(secretId) || StrUtil.isBlank(secretKey)) {
            log.warn("⚠️ COS配置警告：secretId 或 secretKey 未配置，图片上传功能将无法使用");
        }
        
        if (StrUtil.isBlank(region) || StrUtil.isBlank(bucket)) {
            log.error("❌ COS配置错误：region 或 bucket 未配置");
            throw new IllegalStateException("COS配置错误：region 和 bucket 不能为空");
        }
        
        log.info("✅ COS 配置验证通过");
    }

    @Bean
    public COSClient cosClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);
    }
}


