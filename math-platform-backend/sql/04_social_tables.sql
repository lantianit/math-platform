-- ========================================
-- Math Platform 社交互动相关表
-- 版本: v2.0
-- 功能: 点赞、收藏、通知
-- ========================================

USE mathplatform;

-- 清理旧表（按依赖顺序）
DROP TABLE IF EXISTS comment_like;
DROP TABLE IF EXISTS post_favourite;
DROP TABLE IF EXISTS post_like;
DROP TABLE IF EXISTS notification;

-- ============== 社交互动相关表 ==============

-- 帖子点赞表
CREATE TABLE IF NOT EXISTS post_like
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    post_id      BIGINT                                  NOT NULL COMMENT '帖子ID',
    user_id      BIGINT                                  NOT NULL COMMENT '用户ID',
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_post_user (post_id, user_id),
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id)
) COMMENT '帖子点赞' COLLATE = utf8mb4_unicode_ci;

-- 帖子收藏表
CREATE TABLE IF NOT EXISTS post_favourite
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    post_id      BIGINT                                  NOT NULL COMMENT '帖子ID',
    user_id      BIGINT                                  NOT NULL COMMENT '用户ID',
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_post_user (post_id, user_id),
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id)
) COMMENT '帖子收藏' COLLATE = utf8mb4_unicode_ci;

-- 评论点赞表
CREATE TABLE IF NOT EXISTS comment_like
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    comment_id   BIGINT                                  NOT NULL COMMENT '评论ID',
    user_id      BIGINT                                  NOT NULL COMMENT '用户ID',
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_comment_user (comment_id, user_id),
    INDEX idx_comment_id (comment_id),
    INDEX idx_user_id (user_id)
) COMMENT '评论点赞' COLLATE = utf8mb4_unicode_ci;

-- 通知表
CREATE TABLE IF NOT EXISTS notification
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    user_id       BIGINT                                  NOT NULL COMMENT '接收用户ID',
    from_user_id  BIGINT                                  NULL     COMMENT '发送用户ID',
    type          VARCHAR(20)                             NOT NULL COMMENT '通知类型：like/comment/follow/system',
    title         VARCHAR(100)                            NOT NULL COMMENT '通知标题',
    content       VARCHAR(500)                            NULL     COMMENT '通知内容',
    related_id    BIGINT                                  NULL     COMMENT '关联ID（帖子ID、评论ID等）',
    is_read       TINYINT       DEFAULT 0                 NOT NULL COMMENT '是否已读：0-未读 1-已读',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '创建时间',
    read_time     DATETIME                                 NULL     COMMENT '阅读时间',
    INDEX idx_user_id (user_id),
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time)
) COMMENT '通知' COLLATE = utf8mb4_unicode_ci;
