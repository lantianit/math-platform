package com.zh.mathplatform.application.service;

import com.zh.mathplatform.domain.config.entity.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置应用服务接口
 */
public interface SystemConfigApplicationService {

    /**
     * 根据配置键获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值（带默认值）
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 获取整数类型配置值
     */
    Integer getIntConfigValue(String configKey, Integer defaultValue);

    /**
     * 获取布尔类型配置值
     */
    Boolean getBooleanConfigValue(String configKey, Boolean defaultValue);

    /**
     * 获取双精度类型配置值
     */
    Double getDoubleConfigValue(String configKey, Double defaultValue);

    /**
     * 设置配置值
     */
    boolean setConfigValue(String configKey, String configValue, String description);

    /**
     * 批量设置配置
     */
    boolean batchSetConfig(Map<String, String> configMap);

    /**
     * 获取所有配置
     */
    List<SystemConfig> getAllConfigs();

    /**
     * 根据类型获取配置
     */
    List<SystemConfig> getConfigsByType(String type);

    /**
     * 删除配置
     */
    boolean deleteConfig(String configKey);

    /**
     * 重新加载配置缓存
     */
    void reloadConfigCache();
}
