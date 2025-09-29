-- ========================================
-- Math Platform 用户管理相关表
-- 版本: v2.0
-- 功能: 用户、关注、用户行为日志、用户头像审核
-- ========================================

USE mathplatform;

-- 清理旧表（按依赖顺序）
DROP TABLE IF EXISTS user_avatar_review;
DROP TABLE IF EXISTS user_action_log;
DROP TABLE IF EXISTS user_follow;
DROP TABLE IF EXISTS user;

-- ============== 用户相关表 ==============

-- 用户表
CREATE TABLE IF NOT EXISTS user
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    user_account     VARCHAR(50)                             NOT NULL COMMENT '用户账号',
    user_password    VARCHAR(100)                            NOT NULL COMMENT '用户密码',
    user_name        VARCHAR(100)                            NOT NULL COMMENT '用户昵称',
    user_avatar      VARCHAR(500)                            NULL     COMMENT '用户头像',
    user_profile     VARCHAR(1000)                           NULL     COMMENT '用户简介',
    user_role        VARCHAR(20)       DEFAULT 'user'        NOT NULL COMMENT '用户角色：user/admin',
    email            VARCHAR(100)                            NULL     COMMENT '邮箱',
    phone            VARCHAR(20)                             NULL     COMMENT '手机号',
    status           TINYINT       DEFAULT 1                 NOT NULL COMMENT '状态：0-封禁 1-正常',
    last_login_time  DATETIME                                 NULL     COMMENT '最后登录时间',
    last_login_ip    VARCHAR(50)                             NULL     COMMENT '最后登录IP',
    post_count       INT           DEFAULT 0                 NOT NULL COMMENT '发帖数',
    like_count       INT           DEFAULT 0                 NOT NULL COMMENT '获赞数',
    edit_time        DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '编辑时间',
    create_time      DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time      DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete        TINYINT       DEFAULT 0                 NOT NULL COMMENT '是否删除：0-未删除 1-已删除',
    UNIQUE KEY uk_user_account (user_account),
    UNIQUE KEY uk_email (email),
    INDEX idx_user_name (user_name),
    INDEX idx_user_role (user_role),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_is_delete (is_delete)
) COMMENT '用户' COLLATE = utf8mb4_unicode_ci;

-- 关注表
CREATE TABLE IF NOT EXISTS user_follow
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    follower_id    BIGINT                                  NOT NULL COMMENT '关注者ID',
    following_id   BIGINT                                  NOT NULL COMMENT '被关注者ID',
    create_time    DATETIME      DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '创建时间',
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    INDEX idx_follower_id (follower_id),
    INDEX idx_following_id (following_id)
) COMMENT '用户关注' COLLATE = utf8mb4_unicode_ci;

-- 用户行为日志
CREATE TABLE IF NOT EXISTS user_action_log
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id      BIGINT                                  NULL     COMMENT '用户ID',
    action       VARCHAR(50)                             NOT NULL COMMENT '行为类型：login/logout/post/comment/like/view',
    target_type  VARCHAR(20)                             NULL     COMMENT '目标类型：post/comment/user',
    target_id    BIGINT                                  NULL     COMMENT '目标ID',
    ip           VARCHAR(50)                             NULL     COMMENT 'IP地址',
    userAgent    VARCHAR(500)                            NULL     COMMENT '用户代理',
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_target (target_type, target_id),
    INDEX idx_create_time (create_time)
) COMMENT '用户行为日志' COLLATE = utf8mb4_unicode_ci;

-- 用户头像审核表
CREATE TABLE IF NOT EXISTS user_avatar_review
(
    id               BIGINT       NOT NULL PRIMARY KEY COMMENT '审核记录ID（应用侧分配）',
    user_id          BIGINT       NOT NULL COMMENT '用户ID',
    bucket           VARCHAR(128) NOT NULL COMMENT '存储桶',
    object_key       VARCHAR(512) NOT NULL COMMENT '对象键',
    url              VARCHAR(1024)         NULL COMMENT '对象 URL',
    thumbnail_key    VARCHAR(512)          NULL COMMENT '缩略图对象键',
    thumbnail_url    VARCHAR(1024)         NULL COMMENT '缩略图 URL',
    etag             VARCHAR(128)          NULL COMMENT 'ETag',
    sha256           VARCHAR(128)          NULL COMMENT 'SHA256',
    width            INT                    NULL COMMENT '宽',
    height           INT                    NULL COMMENT '高',
    format           VARCHAR(32)            NULL COMMENT '格式',
    size             BIGINT                 NULL COMMENT '字节大小',
    status           INT          NOT NULL COMMENT '状态码',
    risk_labels      JSON                   NULL COMMENT '风险标签',
    machine_score    DOUBLE                 NULL COMMENT '机器分数',
    reason           VARCHAR(512)           NULL COMMENT '驳回原因',
    reviewer_id      BIGINT                 NULL COMMENT '审核人ID',
    submitted_at     DATETIME               NULL COMMENT '提交时间',
    auto_reviewed_at DATETIME               NULL COMMENT '机审时间',
    reviewed_at      DATETIME               NULL COMMENT '终审时间',
    published_at     DATETIME               NULL COMMENT '发布时间',
    last_error       VARCHAR(512)           NULL COMMENT '最后错误',
    KEY idx_user_status (user_id, status)
) COMMENT '用户头像审核' COLLATE = utf8mb4_unicode_ci;
