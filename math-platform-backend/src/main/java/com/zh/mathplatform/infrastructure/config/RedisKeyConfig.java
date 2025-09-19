package com.zh.mathplatform.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Redis Key 配置管理
 * 支持动态环境前缀和应用前缀
 * 
 * @author zh
 */
@Component
public class RedisKeyConfig {

    /**
     * 应用名称前缀
     */
    @Value("${spring.application.name:math-platform}")
    private String appName;

    /**
     * 环境前缀
     */
    @Value("${spring.profiles.active:dev}")
    private String environment;

    /**
     * Redis Key 分隔符
     */
    private static final String SEPARATOR = ":";

    /**
     * 构建完整的 Redis Key
     * 格式: {app}:{env}:{domain}:{key}
     * 
     * @param domain 领域模块
     * @param key    业务键
     * @return 完整的 Redis Key
     */
    public String buildKey(String domain, String key) {
        return String.join(SEPARATOR, appName, environment, domain, key);
    }

    /**
     * 构建带ID的 Redis Key
     * 格式: {app}:{env}:{domain}:{key}:{id}
     */
    public String buildKeyWithId(String domain, String key, Object id) {
        return String.join(SEPARATOR, appName, environment, domain, key, String.valueOf(id));
    }

    /**
     * 构建带多个参数的 Redis Key
     * 格式: {app}:{env}:{domain}:{key}:{param1}:{param2}...
     */
    public String buildKeyWithParams(String domain, String key, Object... params) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(appName).append(SEPARATOR)
                  .append(environment).append(SEPARATOR)
                  .append(domain).append(SEPARATOR)
                  .append(key);
        
        for (Object param : params) {
            keyBuilder.append(SEPARATOR).append(param);
        }
        
        return keyBuilder.toString();
    }

    /**
     * 获取应用前缀
     */
    public String getAppPrefix() {
        return appName + SEPARATOR + environment;
    }

    /**
     * 获取领域前缀
     */
    public String getDomainPrefix(String domain) {
        return String.join(SEPARATOR, appName, environment, domain);
    }
}
