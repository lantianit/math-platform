-- ========================================
-- Math Platform 壁纸功能相关表
-- 版本: v2.0
-- 功能: 学习加油壁纸管理
-- ========================================

USE mathplatform;

-- 清理旧表
DROP TABLE IF EXISTS wallpaper;

-- ============== 壁纸功能相关表 ==============

-- 学习加油壁纸表
CREATE TABLE IF NOT EXISTS wallpaper
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name           VARCHAR(255) NOT NULL COMMENT '壁纸名称',
    description    TEXT COMMENT '壁纸描述',
    url            VARCHAR(512) NOT NULL COMMENT '壁纸URL',
    thumbnail_url  VARCHAR(512) COMMENT '壁纸缩略图URL',
    category       VARCHAR(50) DEFAULT '学习励志' COMMENT '壁纸分类',
    tags           JSON COMMENT '壁纸标签（JSON数组格式）',
    width          INT COMMENT '图片宽度',
    height         INT COMMENT '图片高度',
    file_size      BIGINT COMMENT '文件大小（字节）',
    pic_color      VARCHAR(16) COMMENT '图片主色调（十六进制颜色值，如 0xFF0000）',
    download_count INT         DEFAULT 0 COMMENT '下载次数',
    like_count     INT         DEFAULT 0 COMMENT '点赞数',
    status         INT         DEFAULT 0 COMMENT '状态（0-正常，1-隐藏）',
    user_id        BIGINT       NOT NULL COMMENT '创建用户ID',
    create_time    DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete      TINYINT     DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_download_count (download_count),
    INDEX idx_like_count (like_count),
    INDEX idx_pic_color (pic_color)
) COMMENT ='学习加油壁纸表' COLLATE = utf8mb4_unicode_ci;
