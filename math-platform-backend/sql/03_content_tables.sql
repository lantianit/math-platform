-- ========================================
-- Math Platform 内容管理相关表
-- 版本: v2.0
-- 功能: 分类、标签、帖子、评论
-- ========================================

USE mathplatform;

-- 清理旧表（按依赖顺序）
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS category;

-- ============== 内容管理相关表 ==============

-- 分类表
CREATE TABLE IF NOT EXISTS category
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name         VARCHAR(50)                             NOT NULL COMMENT '分类名称',
    description  VARCHAR(200)                            NULL     COMMENT '分类描述',
    icon         VARCHAR(100)                            NULL     COMMENT '分类图标',
    color        VARCHAR(20)                             NULL     COMMENT '分类颜色',
    sort         INT           DEFAULT 0                 NOT NULL COMMENT '排序',
    status       TINYINT       DEFAULT 1                 NOT NULL COMMENT '状态：0-禁用 1-启用',
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time  DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_name (name),
    INDEX idx_status (status),
    INDEX idx_sort (sort)
) COMMENT '分类' COLLATE = utf8mb4_unicode_ci;

-- 标签表
CREATE TABLE IF NOT EXISTS tag
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    name         VARCHAR(50)                             NOT NULL COMMENT '标签名称',
    color        VARCHAR(20)                             NULL     COMMENT '标签颜色',
    post_count   INT           DEFAULT 0                 NOT NULL COMMENT '帖子数量',
    status       TINYINT       DEFAULT 1                 NOT NULL COMMENT '状态：0-禁用 1-启用',
    create_time  DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time  DATETIME      DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_name (name),
    INDEX idx_status (status),
    INDEX idx_post_count (post_count)
) COMMENT '标签' COLLATE = utf8mb4_unicode_ci;

-- 帖子表
CREATE TABLE IF NOT EXISTS post
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '帖子ID',
    title            VARCHAR(200)                            NOT NULL COMMENT '帖子标题',
    content          TEXT                                    NOT NULL COMMENT '帖子内容',
    content_summary  VARCHAR(500)                            NULL     COMMENT '内容摘要',
    user_id          BIGINT                                  NOT NULL COMMENT '发布者ID',
    category         VARCHAR(50)  DEFAULT 'tech'             NOT NULL COMMENT '分类：tech/question/project/share',
    tags             VARCHAR(1000)                           NULL     COMMENT '标签，JSON格式',
    images           VARCHAR(2000)                           NULL     COMMENT '图片URL，JSON格式',
    view_count       INT           DEFAULT 0                 NOT NULL COMMENT '浏览量',
    like_count       INT           DEFAULT 0                 NOT NULL COMMENT '点赞数',
    comment_count    INT           DEFAULT 0                 NOT NULL COMMENT '评论数',
    favourite_count  INT           DEFAULT 0                 NOT NULL COMMENT '收藏数',
    share_count      INT           DEFAULT 0                 NOT NULL COMMENT '分享数',
    quality_score    DECIMAL(3,2)  DEFAULT 0.00              NOT NULL COMMENT '质量评分',
    hot_score        DECIMAL(10,2) DEFAULT 0.00              NOT NULL COMMENT '热度评分',
    status           TINYINT       DEFAULT 1                 NOT NULL COMMENT '状态：0-删除 1-正常 2-审核中 3-精华',
    is_top           TINYINT       DEFAULT 0                 NOT NULL COMMENT '是否置顶',
    is_hot           TINYINT       DEFAULT 0                 NOT NULL COMMENT '是否热门',
    audit_status     TINYINT       DEFAULT 1                 NOT NULL COMMENT '审核状态：0-待审核 1-通过 2-拒绝',
    audit_time       DATETIME                                 NULL     COMMENT '审核时间',
    audit_user_id    BIGINT                                   NULL     COMMENT '审核人ID',
    audit_remark     VARCHAR(500)                             NULL     COMMENT '审核备注',
    publish_time     DATETIME      DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '发布时间',
    create_time      DATETIME      DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '创建时间',
    update_time      DATETIME      DEFAULT CURRENT_TIMESTAMP  NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete        TINYINT       DEFAULT 0                  NOT NULL COMMENT '是否删除：0-未删除 1-已删除',
    INDEX idx_user_id (user_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_hot_score (hot_score),
    INDEX idx_quality_score (quality_score),
    INDEX idx_view_count (view_count),
    INDEX idx_like_count (like_count),
    INDEX idx_is_top_is_hot (is_top, is_hot),
    INDEX idx_is_delete (is_delete),
    FULLTEXT INDEX ft_title_content (title, content)
) COMMENT '帖子' COLLATE = utf8mb4_unicode_ci;

-- 评论表
CREATE TABLE IF NOT EXISTS comment
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    post_id      BIGINT                                  NOT NULL COMMENT '帖子ID',
    user_id      BIGINT                                  NOT NULL COMMENT '评论者ID',
    content      TEXT                                    NOT NULL COMMENT '评论内容',
    parent_id    BIGINT           DEFAULT NULL            NULL     COMMENT '父评论ID',
    root_id      BIGINT           DEFAULT NULL            NULL     COMMENT '根评论ID',
    like_count   INT              DEFAULT 0               NOT NULL COMMENT '点赞数',
    reply_count  INT              DEFAULT 0               NOT NULL COMMENT '回复数',
    status       TINYINT          DEFAULT 1               NOT NULL COMMENT '状态：0-删除 1-正常',
    create_time  DATETIME         DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time  DATETIME         DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete    TINYINT          DEFAULT 0               NOT NULL COMMENT '是否删除：0-未删除 1-已删除',
    INDEX idx_post_id (post_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_root_id (root_id),
    INDEX idx_create_time (create_time),
    INDEX idx_is_delete (is_delete)
) COMMENT '评论' COLLATE = utf8mb4_unicode_ci;
