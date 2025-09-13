-- ========================================
-- Math Platform 数据库初始化脚本（统一下划线命名、幂等执行）
-- 版本: v2.0
-- 说明:
-- 1) 脚本将按顺序 DROP 旧表并重建，避免历史驼峰列名导致的列不匹配
-- 2) 与后端全局配置（驼峰转下划线）保持一致
-- 3) 初始化基础配置、分类、标签、管理员账户等数据
-- ========================================

-- 建库与选库
CREATE DATABASE IF NOT EXISTS mathplatform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mathplatform;

-- ============== 清理旧表（按依赖顺序） ==============
DROP TABLE IF EXISTS user_avatar_review;
DROP TABLE IF EXISTS user_action_log;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS comment_like;
DROP TABLE IF EXISTS post_favourite;
DROP TABLE IF EXISTS post_like;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS user_follow;
DROP TABLE IF EXISTS system_config;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;

-- ============== 创建基础表 ==============

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

-- ============== 业务表 ==============

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

-- 点赞/收藏表
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

-- 用户头像审核表（依赖 user）
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

-- ============== 初始化基础数据（幂等） ==============

INSERT IGNORE INTO category (name, description, icon, color, sort) VALUES
('技术讨论', '分享技术心得、讨论技术问题', 'CodeOutlined', '#1890ff', 1),
('问题求助', '遇到问题时寻求帮助', 'QuestionCircleOutlined', '#52c41a', 2),
('项目展示', '展示个人或团队项目', 'ProjectOutlined', '#722ed1', 3),
('经验分享', '分享学习和工作经验', 'ShareAltOutlined', '#fa8c16', 4),
('资源推荐', '推荐优质学习资源', 'StarOutlined', '#eb2f96', 5),
('闲聊灌水', '日常交流和闲聊', 'CommentOutlined', '#13c2c2', 6);

INSERT IGNORE INTO tag (name, color) VALUES
('前端', '#1890ff'),
('后端', '#52c41a'),
('数据库', '#722ed1'),
('算法', '#fa8c16'),
('框架', '#eb2f96'),
('工具', '#13c2c2'),
('部署', '#f5222d'),
('性能优化', '#a0d911'),
('安全', '#faad14'),
('架构设计', '#2f54eb');

INSERT IGNORE INTO system_config (config_key, config_value, type, description) VALUES
('site.name', 'Math Platform', 'string', '网站名称'),
('site.description', '企业级数学论坛平台', 'string', '网站描述'),
('site.keywords', '数学,论坛,技术交流,学习', 'string', '网站关键词'),
('post.max_length', '10000', 'number', '帖子最大长度'),
('comment.max_length', '1000', 'number', '评论最大长度'),
('upload.max_size', '10485760', 'number', '上传文件最大大小(字节)'),
('user.default_avatar', '/images/default-avatar.png', 'string', '默认头像'),
('notification.enabled', 'true', 'boolean', '是否启用通知功能');

-- 初始化管理员与演示账号（幂等）
INSERT INTO user (user_account, user_password, user_name, user_avatar, user_profile, user_role, status)
SELECT * FROM (
  SELECT 'admin'    AS user_account, 'b0dd3697a192885d7c055db46155b26a' AS user_password, '系统管理员' AS user_name, '/images/avatars/admin.png' AS user_avatar, '数学平台系统管理员，负责平台运营和管理工作。' AS user_profile, 'admin' AS user_role, 1 AS status
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM user WHERE user_account = tmp.user_account);

INSERT IGNORE INTO user (user_account, user_password, user_name, user_avatar, user_profile, user_role, status) VALUES
('zhangsan', 'b0dd3697a192885d7c055db46155b26a', '张三', '/images/avatars/user2.png', '数学爱好者，专注于高等数学和线性代数研究。', 'user', 1),
('lisi',     'b0dd3697a192885d7c055db46155b26a', '李四', '/images/avatars/user3.png', '计算机科学学生，对算法和数据结构很感兴趣。', 'user', 1),
('wangwu',   'b0dd3697a192885d7c055db46155b26a', '王五', '/images/avatars/user4.png', '软件工程师，擅长前端开发和用户体验设计。', 'user', 1),
('zhaoliu',  'b0dd3697a192885d7c055db46155b26a', '赵六', '/images/avatars/user5.png', '数据科学家，专注于机器学习和统计分析。', 'user', 1),
('sunqi',    'b0dd3697a192885d7c055db46155b26a', '孙七', '/images/avatars/user6.png', '博士研究生，研究方向为应用数学和数值计算。', 'user', 1),
('zhouba',   'b0dd3697a192885d7c055db46155b26a', '周八', '/images/avatars/user7.png', '高中数学老师，热爱教育事业，善于解答学生问题。', 'user', 1),
('wujiu',    'b0dd3697a192885d7c055db46155b26a', '吴九', '/images/avatars/user8.png', '大学数学教授，专业领域为拓扑学和几何学。', 'user', 1),
('zhengshi', 'b0dd3697a192885d7c055db46155b26a', '郑十', '/images/avatars/user9.png', '数学竞赛教练，培养了众多优秀的数学竞赛选手。', 'user', 1),
('liuyi',    'b0dd3697a192885d7c055db46155b26a', '刘一', '/images/avatars/user10.png', '在读研究生，对数论和代数几何有深入研究。', 'user', 1);

-- ============== 示例数据：帖子与互动 ==============

-- 示例帖子（按标题幂等）
INSERT INTO post (title, content, content_summary, user_id, category, images, view_count, like_count, comment_count, favourite_count, share_count, quality_score, hot_score, status, is_top, is_hot, audit_status, publish_time, create_time, update_time, is_delete)
SELECT * FROM (
  SELECT '欢迎来到 Math Platform' AS title,
         '这是平台的第一篇帖子，欢迎大家分享与交流。' AS content,
         '平台第一帖' AS content_summary,
         (SELECT id FROM user WHERE user_account = 'admin' LIMIT 1) AS user_id,
         'tech' AS category,
         NULL AS images,
         100 AS view_count, 10 AS like_count, 2 AS comment_count, 1 AS favourite_count, 0 AS share_count,
         4.50 AS quality_score, 12.00 AS hot_score,
         1 AS status, 0 AS is_top, 1 AS is_hot, 1 AS audit_status,
         NOW() AS publish_time, NOW() AS create_time, NOW() AS update_time,
         0 AS is_delete
) t
WHERE NOT EXISTS (
  SELECT 1 FROM post WHERE title = '欢迎来到 Math Platform' AND user_id = (SELECT id FROM user WHERE user_account = 'admin' LIMIT 1)
);

INSERT INTO post (title, content, content_summary, user_id, category, images, view_count, like_count, comment_count, favourite_count, share_count, quality_score, hot_score, status, is_top, is_hot, audit_status, publish_time, create_time, update_time, is_delete)
SELECT * FROM (
  SELECT '第一篇技术分享：算法入门' AS title,
         '本文概述算法基础，并给出学习路线与资料推荐。' AS content,
         '算法入门与资料' AS content_summary,
         (SELECT id FROM user WHERE user_account = 'zhangsan' LIMIT 1) AS user_id,
         'tech' AS category,
         NULL AS images,
         80 AS view_count, 6 AS like_count, 1 AS comment_count, 1 AS favourite_count, 0 AS share_count,
         4.20 AS quality_score, 9.00 AS hot_score,
         1 AS status, 0 AS is_top, 0 AS is_hot, 1 AS audit_status,
         NOW() AS publish_time, NOW() AS create_time, NOW() AS update_time,
         0 AS is_delete
) t
WHERE NOT EXISTS (
  SELECT 1 FROM post WHERE title = '第一篇技术分享：算法入门' AND user_id = (SELECT id FROM user WHERE user_account = 'zhangsan' LIMIT 1)
);

-- 示例评论
INSERT INTO comment (post_id, user_id, content, parent_id, root_id, like_count, reply_count, status, create_time, update_time, is_delete)
SELECT * FROM (
  SELECT (SELECT id FROM post WHERE title = '欢迎来到 Math Platform' LIMIT 1) AS post_id,
         (SELECT id FROM user WHERE user_account = 'lisi' LIMIT 1) AS user_id,
         '很高兴加入社区！' AS content,
         NULL AS parent_id,
         NULL AS root_id,
         1 AS like_count,
         0 AS reply_count,
         1 AS status,
         NOW() AS create_time,
         NOW() AS update_time,
         0 AS is_delete
) t
WHERE NOT EXISTS (
  SELECT 1 FROM comment WHERE post_id = (SELECT id FROM post WHERE title = '欢迎来到 Math Platform' LIMIT 1) AND user_id = (SELECT id FROM user WHERE user_account = 'lisi' LIMIT 1) AND content = '很高兴加入社区！'
);

-- 点赞 / 收藏 / 评论点赞
INSERT IGNORE INTO post_like (post_id, user_id, create_time)
SELECT (SELECT id FROM post WHERE title = '欢迎来到 Math Platform' LIMIT 1), (SELECT id FROM user WHERE user_account = 'wangwu' LIMIT 1), NOW();

INSERT IGNORE INTO post_favourite (post_id, user_id, create_time)
SELECT (SELECT id FROM post WHERE title = '第一篇技术分享：算法入门' LIMIT 1), (SELECT id FROM user WHERE user_account = 'zhaoliu' LIMIT 1), NOW();

INSERT IGNORE INTO comment_like (comment_id, user_id, create_time)
SELECT (SELECT id FROM comment WHERE post_id = (SELECT id FROM post WHERE title = '欢迎来到 Math Platform' LIMIT 1) AND user_id = (SELECT id FROM user WHERE user_account = 'lisi' LIMIT 1) LIMIT 1), (SELECT id FROM user WHERE user_account = 'sunqi' LIMIT 1), NOW();

-- 关注关系
INSERT IGNORE INTO user_follow (follower_id, following_id, create_time)
SELECT (SELECT id FROM user WHERE user_account = 'zhangsan' LIMIT 1), (SELECT id FROM user WHERE user_account = 'admin' LIMIT 1), NOW();

-- 通知示例
INSERT IGNORE INTO notification (user_id, from_user_id, type, title, content, related_id, is_read, create_time)
SELECT (SELECT id FROM user WHERE user_account = 'admin' LIMIT 1), NULL, 'system', '欢迎通知', '欢迎使用 Math Platform！', NULL, 0, NOW();

-- 举报示例
INSERT IGNORE INTO report (reporter_id, target_type, target_id, reason, description, status, create_time)
SELECT (SELECT id FROM user WHERE user_account = 'lisi' LIMIT 1), 'post', (SELECT id FROM post WHERE title = '第一篇技术分享：算法入门' LIMIT 1), 'other', '测试举报，无需处理', 0, NOW();

-- 用户行为日志
INSERT IGNORE INTO user_action_log (user_id, action, target_type, target_id, ip, userAgent, create_time)
SELECT (SELECT id FROM user WHERE user_account = 'zhangsan' LIMIT 1), 'login', NULL, NULL, '127.0.0.1', 'Mozilla/5.0', NOW();

INSERT IGNORE INTO user_action_log (user_id, action, target_type, target_id, ip, userAgent, create_time)
SELECT (SELECT id FROM user WHERE user_account = 'zhangsan' LIMIT 1), 'view', 'post', (SELECT id FROM post WHERE title = '欢迎来到 Math Platform' LIMIT 1), '127.0.0.1', 'Mozilla/5.0', NOW();

-- 用户头像审核示例（PENDING）
INSERT INTO user_avatar_review (id, user_id, bucket, object_key, url, thumbnail_key, thumbnail_url, etag, sha256, width, height, format, size, status, submitted_at)
SELECT * FROM (
  SELECT 10001 AS id,
         (SELECT id FROM user WHERE user_account = 'zhangsan' LIMIT 1) AS user_id,
         'mp-avatar-quarantine' AS bucket,
         'avatar/quarantine/user_zhangsan/2025-09-12/abc123.png' AS object_key,
         'https://cdn.example.com/avatar/quarantine/user_zhangsan/2025-09-12/abc123.png' AS url,
         NULL AS thumbnail_key,
         NULL AS thumbnail_url,
         'etag-abc123' AS etag,
         'sha256-abc123' AS sha256,
         256 AS width,
         256 AS height,
         'png' AS format,
         123456 AS size,
         0 AS status,
         NOW() AS submitted_at
) t
WHERE NOT EXISTS (SELECT 1 FROM user_avatar_review WHERE id = 10001);