-- ========================================
-- Math Platform 企业级论坛数据库完整初始化脚本 (修复版)
-- 版本: v1.1 (修复版)
-- 创建日期: 2025-09-02
-- 描述: 修复了字段名错误和重复数据问题的完整脚本
-- 使用说明: 执行此脚本将完成数据库的完整设置
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mathplatform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mathplatform;

-- ========================================
-- 第一部分: 数据库表结构初始化
-- ========================================

-- 用户表
CREATE TABLE IF NOT EXISTS user
(
    id             bigint auto_increment comment '用户ID' primary key,
    userAccount    varchar(50)                            not null comment '用户账号',
    userPassword   varchar(100)                           not null comment '用户密码',
    userName       varchar(100)                           not null comment '用户昵称',
    userAvatar     varchar(500)                           null comment '用户头像',
    userProfile    varchar(1000)                          null comment '用户简介',
    email          varchar(100)                           null comment '邮箱',
    phone          varchar(20)                            null comment '手机号',
    postCount      int          default 0                 not null comment '发帖数',
    likeCount      int          default 0                 not null comment '获赞数',
    -- 状态字段
    status         tinyint      default 1                 not null comment '状态：0-封禁 1-正常',
    lastLoginTime  datetime                               null comment '最后登录时间',
    lastLoginIp    varchar(50)                           null comment '最后登录IP',
    -- 时间字段
    editTime       datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除：0-未删除 1-已删除',
    -- 索引
    UNIQUE KEY uk_userAccount (userAccount),
    UNIQUE KEY uk_email (email),
    INDEX idx_userName (userName),
    INDEX idx_status (status),
    INDEX idx_createTime (createTime),
    INDEX idx_isDelete (isDelete)
    ) comment '用户' collate = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS post
(
    id             bigint auto_increment comment '帖子ID' primary key,
    title          varchar(200)                           not null comment '帖子标题',
    content        text                                   not null comment '帖子内容',
    contentSummary varchar(500)                           null comment '内容摘要',
    userId         bigint                                 not null comment '发布者ID',
    category       varchar(50)  default 'tech'            not null comment '分类：tech/question/project/share',
    images         varchar(2000)                          null comment '图片URL，JSON格式',
    -- 统计字段
    viewCount      int          default 0                 not null comment '浏览量',
    likeCount      int          default 0                 not null comment '点赞数',
    commentCount   int          default 0                 not null comment '评论数',
    favouriteCount int          default 0                 not null comment '收藏数',
    shareCount     int          default 0                 not null comment '分享数',
    -- 质量评分
    qualityScore   decimal(3,2) default 0.00              not null comment '质量评分',
    hotScore       decimal(10,2) default 0.00             not null comment '热度评分',
    -- 状态字段
    status         tinyint      default 1                 not null comment '状态：0-删除 1-正常 2-审核中 3-精华',
    isTop          tinyint      default 0                 not null comment '是否置顶',
    isHot          tinyint      default 0                 not null comment '是否热门',
    -- 审核字段
    auditStatus    tinyint      default 1                 not null comment '审核状态：0-待审核 1-通过 2-拒绝',
    auditTime      datetime                               null comment '审核时间',
    auditUserId    bigint                                 null comment '审核人ID',
    auditRemark    varchar(500)                           null comment '审核备注',
    -- 时间字段
    publishTime    datetime     default CURRENT_TIMESTAMP not null comment '发布时间',
    createTime     datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint      default 0                 not null comment '是否删除：0-未删除 1-已删除',
    -- 索引
    INDEX idx_userId (userId),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_createTime (createTime),
    INDEX idx_hotScore (hotScore),
    INDEX idx_qualityScore (qualityScore),
    INDEX idx_viewCount (viewCount),
    INDEX idx_likeCount (likeCount),
    INDEX idx_isTop_isHot (isTop, isHot),
    INDEX idx_isDelete (isDelete),
    FULLTEXT INDEX ft_title_content (title, content)
    ) comment '帖子' collate = utf8mb4_unicode_ci;

-- 评论表
CREATE TABLE IF NOT EXISTS comment
(
    id         bigint auto_increment comment '评论ID' primary key,
    postId     bigint                                 not null comment '帖子ID',
    userId     bigint                                 not null comment '评论者ID',
    content    text                                   not null comment '评论内容',
    parentId   bigint       default null               null comment '父评论ID',
    rootId     bigint       default null               null comment '根评论ID',
    likeCount  int          default 0                 not null comment '点赞数',
    replyCount int          default 0                 not null comment '回复数',
    status     tinyint      default 1                 not null comment '状态：0-删除 1-正常',
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint      default 0                 not null comment '是否删除：0-未删除 1-已删除',
    INDEX idx_postId (postId),
    INDEX idx_userId (userId),
    INDEX idx_parentId (parentId),
    INDEX idx_rootId (rootId),
    INDEX idx_createTime (createTime),
    INDEX idx_isDelete (isDelete)
    ) comment '评论' collate = utf8mb4_unicode_ci;

-- 帖子点赞表
CREATE TABLE IF NOT EXISTS post_like
(
    id         bigint auto_increment comment 'ID' primary key,
    postId     bigint                               not null comment '帖子ID',
    userId     bigint                               not null comment '用户ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    UNIQUE KEY uk_post_user (postId, userId),
    INDEX idx_postId (postId),
    INDEX idx_userId (userId)
    ) comment '帖子点赞' collate = utf8mb4_unicode_ci;

-- 帖子收藏表
CREATE TABLE IF NOT EXISTS post_favourite
(
    id         bigint auto_increment comment 'ID' primary key,
    postId     bigint                               not null comment '帖子ID',
    userId     bigint                               not null comment '用户ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    UNIQUE KEY uk_post_user (postId, userId),
    INDEX idx_postId (postId),
    INDEX idx_userId (userId)
    ) comment '帖子收藏' collate = utf8mb4_unicode_ci;

-- 评论点赞表
CREATE TABLE IF NOT EXISTS comment_like
(
    id         bigint auto_increment comment 'ID' primary key,
    commentId  bigint                               not null comment '评论ID',
    userId     bigint                               not null comment '用户ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    UNIQUE KEY uk_comment_user (commentId, userId),
    INDEX idx_commentId (commentId),
    INDEX idx_userId (userId)
    ) comment '评论点赞' collate = utf8mb4_unicode_ci;

-- 用户关注表
CREATE TABLE IF NOT EXISTS user_follow
(
    id          bigint auto_increment comment 'ID' primary key,
    followerId  bigint                               not null comment '关注者ID',
    followingId bigint                               not null comment '被关注者ID',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    UNIQUE KEY uk_follower_following (followerId, followingId),
    INDEX idx_followerId (followerId),
    INDEX idx_followingId (followingId)
    ) comment '用户关注' collate = utf8mb4_unicode_ci;

-- 分类表
CREATE TABLE IF NOT EXISTS category
(
    id          bigint auto_increment comment '分类ID' primary key,
    name        varchar(50)                            not null comment '分类名称',
    description varchar(200)                           null comment '分类描述',
    icon        varchar(100)                           null comment '分类图标',
    color       varchar(20)                            null comment '分类颜色',
    sort        int          default 0                 not null comment '排序',
    status      tinyint      default 1                 not null comment '状态：0-禁用 1-启用',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    UNIQUE KEY uk_name (name),
    INDEX idx_status (status),
    INDEX idx_sort (sort)
    ) comment '分类' collate = utf8mb4_unicode_ci;

-- 标签表
CREATE TABLE IF NOT EXISTS tag
(
    id         bigint auto_increment comment '标签ID' primary key,
    name       varchar(50)                            not null comment '标签名称',
    color      varchar(20)                            null comment '标签颜色',
    postCount  int          default 0                 not null comment '帖子数量',
    status     tinyint      default 1                 not null comment '状态：0-禁用 1-启用',
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    UNIQUE KEY uk_name (name),
    INDEX idx_status (status),
    INDEX idx_postCount (postCount)
    ) comment '标签' collate = utf8mb4_unicode_ci;

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config
(
    id          bigint auto_increment comment '配置ID' primary key,
    configKey   varchar(100)                           not null comment '配置键',
    configValue text                                   null comment '配置值',
    type        varchar(20)  default 'string'          not null comment '配置类型：string/number/boolean/json',
    description varchar(200)                           null comment '配置描述',
    status      tinyint      default 1                 not null comment '状态：0-禁用 1-启用',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    UNIQUE KEY uk_configKey (configKey),
    INDEX idx_status (status)
    ) comment '系统配置' collate = utf8mb4_unicode_ci;

-- 通知表
CREATE TABLE IF NOT EXISTS notification
(
    id         bigint auto_increment comment '通知ID' primary key,
    userId     bigint                                 not null comment '接收用户ID',
    fromUserId bigint                                 null comment '发送用户ID',
    type       varchar(20)                            not null comment '通知类型：like/comment/follow/system',
    title      varchar(100)                           not null comment '通知标题',
    content    varchar(500)                           null comment '通知内容',
    relatedId  bigint                                 null comment '关联ID（帖子ID、评论ID等）',
    isRead     tinyint      default 0                 not null comment '是否已读：0-未读 1-已读',
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    readTime   datetime                               null comment '阅读时间',
    INDEX idx_userId (userId),
    INDEX idx_fromUserId (fromUserId),
    INDEX idx_type (type),
    INDEX idx_isRead (isRead),
    INDEX idx_createTime (createTime)
    ) comment '通知' collate = utf8mb4_unicode_ci;

-- 举报表
CREATE TABLE IF NOT EXISTS report
(
    id          bigint auto_increment comment '举报ID' primary key,
    reporterId  bigint                                 not null comment '举报者ID',
    targetType  varchar(20)                            not null comment '举报目标类型：post/comment/user',
    targetId    bigint                                 not null comment '举报目标ID',
    reason      varchar(20)                            not null comment '举报原因：spam/abuse/inappropriate/other',
    description varchar(500)                           null comment '举报描述',
    status      tinyint      default 0                 not null comment '处理状态：0-待处理 1-已处理 2-已忽略',
    handleTime  datetime                               null comment '处理时间',
    handleUserId bigint                                null comment '处理人ID',
    handleResult varchar(500)                          null comment '处理结果',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    INDEX idx_reporterId (reporterId),
    INDEX idx_target (targetType, targetId),
    INDEX idx_status (status),
    INDEX idx_createTime (createTime)
    ) comment '举报' collate = utf8mb4_unicode_ci;

-- 用户行为日志表
CREATE TABLE IF NOT EXISTS user_action_log
(
    id         bigint auto_increment comment '日志ID' primary key,
    userId     bigint                                 null comment '用户ID',
    action     varchar(50)                            not null comment '行为类型：login/logout/post/comment/like/view',
    targetType varchar(20)                            null comment '目标类型：post/comment/user',
    targetId   bigint                                 null comment '目标ID',
    ip         varchar(50)                            null comment 'IP地址',
    userAgent  varchar(500)                           null comment '用户代理',
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    INDEX idx_userId (userId),
    INDEX idx_action (action),
    INDEX idx_target(targetType, targetId),
    INDEX idx_createTime (createTime)
    ) comment '用户行为日志' collate = utf8mb4_unicode_ci;

-- ========================================
-- ========================================

-- ========================================
-- ========================================

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

INSERT IGNORE INTO system_config (configKey, configValue, type, description) VALUES
                                                                                 ('site.name', 'Math Platform', 'string', '网站名称'),
                                                                                 ('site.description', '企业级数学论坛平台', 'string', '网站描述'),
                                                                                 ('site.keywords', '数学,论坛,技术交流,学习', 'string', '网站关键词'),
                                                                                 ('post.max_length', '10000', 'number', '帖子最大长度'),
                                                                                 ('comment.max_length', '1000', 'number', '评论最大长度'),
                                                                                 ('upload.max_size', '10485760', 'number', '上传文件最大大小(字节)'),
                                                                                 ('user.default_avatar', '/images/default-avatar.png', 'string', '默认头像'),
                                                                                 ('notification.enabled', 'true', 'boolean', '是否启用通知功能');

-- ========================================
-- ========================================

INSERT INTO user (userAccount, userPassword, userName, userAvatar, userProfile, userRole)
SELECT * FROM (
                  SELECT 'admin' as userAccount, 'b0dd3697a192885d7c055db46155b26a' as userPassword, '系统管理员' as userName, '/images/avatars/admin.png' as userAvatar, '数学平台系统管理员，负责平台运营和管理工作。' as userProfile, 'admin' as userRole
                  UNION ALL
                  SELECT 'zhangsan', 'b0dd3697a192885d7c055db46155b26a', '张三', '/images/avatars/user2.png', '数学爱好者，专注于高等数学和线性代数研究。', 'user'
                  UNION ALL
                  SELECT 'lisi', 'b0dd3697a192885d7c055db46155b26a', '李四', '/images/avatars/user3.png', '计算机科学学生，对算法和数据结构很感兴趣。', 'user'
                  UNION ALL
                  SELECT 'wangwu', 'b0dd3697a192885d7c055db46155b26a', '王五', '/images/avatars/user4.png', '软件工程师，擅长前端开发和用户体验设计。', 'user'
                  UNION ALL
                  SELECT 'zhaoliu', 'b0dd3697a192885d7c055db46155b26a', '赵六', '/images/avatars/user5.png', '数据科学家，专注于机器学习和统计分析。', 'user'
                  UNION ALL
                  SELECT 'sunqi', 'b0dd3697a192885d7c055db46155b26a', '孙七', '/images/avatars/user6.png', '博士研究生，研究方向为应用数学和数值计算。', 'user'
                  UNION ALL
                  SELECT 'zhouba', 'b0dd3697a192885d7c055db46155b26a', '周八', '/images/avatars/user7.png', '高中数学老师，热爱教育事业，善于解答学生问题。', 'user'
                  UNION ALL
                  SELECT 'wujiu', 'b0dd3697a192885d7c055db46155b26a', '吴九', '/images/avatars/user8.png', '大学数学教授，专业领域为拓扑学和几何学。', 'user'
                  UNION ALL
                  SELECT 'zhengshi', 'b0dd3697a192885d7c055db46155b26a', '郑十', '/images/avatars/user9.png', '数学竞赛教练，培养了众多优秀的数学竞赛选手。', 'user'
                  UNION ALL
                  SELECT 'liuyi', 'b0dd3697a192885d7c055db46155b26a', '刘一', '/images/avatars/user10.png', '在读研究生，对数论和代数几何有深入研究。', 'user'
              ) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM user WHERE userAccount = tmp.userAccount);