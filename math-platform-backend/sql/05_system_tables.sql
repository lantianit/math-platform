-- ========================================
-- Math Platform 系统管理相关表
-- 版本: v2.0
-- 功能: 系统配置、举报管理
-- ========================================

USE mathplatform;

-- 清理旧表（按依赖顺序）
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS system_config;

-- ============== 系统管理相关表 ==============

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key    VARCHAR(100)                            NOT NULL COMMENT '配置键',
    config_value  TEXT                                    NULL     COMMENT '配置值',
    type          VARCHAR(20)  DEFAULT 'string'           NOT NULL COMMENT '配置类型：string/number/boolean/json',
    description   VARCHAR(200)                            NULL     COMMENT '配置描述',
    status        TINYINT      DEFAULT 1                  NOT NULL COMMENT '状态：0-禁用 1-启用',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '创建时间',
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP  NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_config_key (config_key),
    INDEX idx_status (status)
) COMMENT '系统配置' COLLATE = utf8mb4_unicode_ci;

-- 举报表
CREATE TABLE IF NOT EXISTS report
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '举报ID',
    reporter_id    BIGINT                                  NOT NULL COMMENT '举报者ID',
    target_type    VARCHAR(20)                             NOT NULL COMMENT '举报目标类型：post/comment/user',
    target_id      BIGINT                                  NOT NULL COMMENT '举报目标ID',
    reason         VARCHAR(20)                             NOT NULL COMMENT '举报原因：spam/abuse/inappropriate/other',
    description    VARCHAR(500)                            NULL     COMMENT '举报描述',
    status         TINYINT       DEFAULT 0                 NOT NULL COMMENT '处理状态：0-待处理 1-已处理 2-已忽略',
    handle_time    DATETIME                                 NULL     COMMENT '处理时间',
    handle_user_id BIGINT                                  NULL     COMMENT '处理人ID',
    handle_result  VARCHAR(500)                            NULL     COMMENT '处理结果',
    create_time    DATETIME      DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '创建时间',
    INDEX idx_reporter_id (reporter_id),
    INDEX idx_target (target_type, target_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) COMMENT '举报' COLLATE = utf8mb4_unicode_ci;
