-- ========================================
-- Math Platform 企业级论坛数据库优化版本
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mathplatform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mathplatform;

-- 用户表 (优化版)
CREATE TABLE IF NOT EXISTS user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    -- 新增字段
    gender       tinyint      default 0                 null comment '性别：0-未知 1-男 2-女',
    email        varchar(256)                           null comment '邮箱',
    phone        varchar(20)                            null comment '手机号',
    birthday     date                                   null comment '生日',
    location     varchar(100)                           null comment '所在地',
    website      varchar(500)                           null comment '个人网站',
    -- 统计字段
    followingCount int         default 0                 not null comment '关注数',
    followerCount  int         default 0                 not null comment '粉丝数',
    postCount      int         default 0                 not null comment '发帖数',
    likeCount      int         default 0                 not null comment '获赞数',
    -- 状态字段
    status       tinyint      default 1                 not null comment '状态：0-封禁 1-正常',
    lastLoginTime datetime                               null comment '最后登录时间',
    lastLoginIp   varchar(50)                           null comment '最后登录IP',
    -- 时间字段
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    -- 索引
    UNIQUE KEY uk_userAccount (userAccount),
    UNIQUE KEY uk_email (email),
    INDEX idx_userName (userName),
    INDEX idx_status (status),
    INDEX idx_createTime (createTime)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表 (优化版)
CREATE TABLE IF NOT EXISTS post
(
    id             bigint auto_increment comment '帖子ID' primary key,
    title          varchar(200)                           not null comment '帖子标题',
    content        text                                   not null comment '帖子内容',
    contentSummary varchar(500)                           null comment '内容摘要',
    userId         bigint                                 not null comment '发布者ID',
    category       varchar(50)  default 'tech'            not null comment '分类：tech/question/project/share',
    tags           varchar(500)                           null comment '标签，JSON格式',
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
    FULLTEXT INDEX ft_title_content (title, content)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 评论表 (优化版)
CREATE TABLE IF NOT EXISTS comment
(
    id         bigint auto_increment comment '评论ID' primary key,
    postId     bigint                                 not null comment '帖子ID',
    userId     bigint                                 not null comment '评论者ID',
    content    text                                   not null comment '评论内容',
    parentId   bigint       default null               null comment '父评论ID',
    rootId     bigint       default null               null comment '根评论ID（用于楼层显示）',
    replyToId  bigint       default null               null comment '回复目标用户ID',
    -- 统计字段
    likeCount  int          default 0                 not null comment '点赞数',
    replyCount int          default 0                 not null comment '回复数',
    -- 状态字段
    status     tinyint      default 1                 not null comment '状态：0-删除 1-正常',
    -- 时间字段
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    -- 索引
    INDEX idx_postId (postId),
    INDEX idx_userId (userId),
    INDEX idx_parentId (parentId),
    INDEX idx_rootId (rootId),
    INDEX idx_createTime (createTime)
) comment '评论' collate = utf8mb4_unicode_ci;

-- 帖子点赞表 (优化版)
CREATE TABLE IF NOT EXISTS post_like
(
    id         bigint auto_increment comment 'ID' primary key,
    postId     bigint                                 not null comment '帖子ID',
    userId     bigint                                 not null comment '用户ID',
    type       tinyint      default 1                 not null comment '类型：1-点赞 2-点踩',
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint      default 0                 not null comment '是否删除',
    -- 索引
    UNIQUE KEY uk_post_user (postId, userId),
    INDEX idx_userId (userId),
    INDEX idx_createTime (createTime)
) comment '帖子点赞' collate = utf8mb4_unicode_ci;

-- 评论点赞表 (新增)
CREATE TABLE IF NOT EXISTS comment_like
(
    id         bigint auto_increment comment 'ID' primary key,
    commentId  bigint                                 not null comment '评论ID',
    userId     bigint                                 not null comment '用户ID',
    type       tinyint      default 1                 not null comment '类型：1-点赞 2-点踩',
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint      default 0                 not null comment '是否删除',
    -- 索引
    UNIQUE KEY uk_comment_user (commentId, userId),
    INDEX idx_userId (userId),
    INDEX idx_createTime (createTime)
) comment '评论点赞' collate = utf8mb4_unicode_ci;

-- 帖子收藏表 (优化版)
CREATE TABLE IF NOT EXISTS post_favourite
(
    id         bigint auto_increment comment 'ID' primary key,
    postId     bigint                                 not null comment '帖子ID',
    userId     bigint                                 not null comment '用户ID',
    folderId   bigint       default null               null comment '收藏夹ID',
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint      default 0                 not null comment '是否删除',
    -- 索引
    UNIQUE KEY uk_post_user (postId, userId),
    INDEX idx_userId (userId),
    INDEX idx_folderId (folderId),
    INDEX idx_createTime (createTime)
) comment '帖子收藏' collate = utf8mb4_unicode_ci;

-- 收藏夹表 (新增)
CREATE TABLE IF NOT EXISTS favourite_folder
(
    id          bigint auto_increment comment 'ID' primary key,
    userId      bigint                                 not null comment '用户ID',
    name        varchar(100)                           not null comment '收藏夹名称',
    description varchar(500)                           null comment '收藏夹描述',
    isPublic    tinyint      default 0                 not null comment '是否公开：0-私有 1-公开',
    postCount   int          default 0                 not null comment '帖子数量',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除',
    -- 索引
    INDEX idx_userId (userId),
    INDEX idx_createTime (createTime)
) comment '收藏夹' collate = utf8mb4_unicode_ci;

-- 用户关注表 (优化版)
CREATE TABLE IF NOT EXISTS user_follow
(
    id          bigint auto_increment comment 'ID' primary key,
    followerId  bigint                                 not null comment '关注者ID',
    followingId bigint                                 not null comment '被关注者ID',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除',
    -- 索引
    UNIQUE KEY uk_follower_following (followerId, followingId),
    INDEX idx_followerId (followerId),
    INDEX idx_followingId (followingId),
    INDEX idx_createTime (createTime)
) comment '用户关注' collate = utf8mb4_unicode_ci;

-- 话题表 (新增)
CREATE TABLE IF NOT EXISTS topic
(
    id          bigint auto_increment comment 'ID' primary key,
    name        varchar(100)                           not null comment '话题名称',
    description varchar(500)                           null comment '话题描述',
    avatar      varchar(1024)                          null comment '话题头像',
    postCount   int          default 0                 not null comment '帖子数量',
    followCount int          default 0                 not null comment '关注数量',
    isHot       tinyint      default 0                 not null comment '是否热门',
    status      tinyint      default 1                 not null comment '状态：0-禁用 1-正常',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除',
    -- 索引
    UNIQUE KEY uk_name (name),
    INDEX idx_postCount (postCount),
    INDEX idx_followCount (followCount),
    INDEX idx_isHot (isHot),
    INDEX idx_status (status)
) comment '话题' collate = utf8mb4_unicode_ci;

-- 帖子话题关联表 (新增)
CREATE TABLE IF NOT EXISTS post_topic
(
    id       bigint auto_increment comment 'ID' primary key,
    postId   bigint                                 not null comment '帖子ID',
    topicId  bigint                                 not null comment '话题ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    -- 索引
    UNIQUE KEY uk_post_topic (postId, topicId),
    INDEX idx_postId (postId),
    INDEX idx_topicId (topicId)
) comment '帖子话题关联' collate = utf8mb4_unicode_ci;

-- 用户消息表 (新增)
CREATE TABLE IF NOT EXISTS user_message
(
    id           bigint auto_increment comment 'ID' primary key,
    fromUserId   bigint                                 null comment '发送者ID（系统消息为null）',
    toUserId     bigint                                 not null comment '接收者ID',
    type         tinyint                                not null comment '消息类型：1-系统消息 2-点赞 3-评论 4-关注 5-私信',
    title        varchar(200)                           null comment '消息标题',
    content      text                                   null comment '消息内容',
    relatedId    bigint                                 null comment '关联ID（如帖子ID、评论ID等）',
    relatedType  varchar(50)                            null comment '关联类型',
    isRead       tinyint      default 0                 not null comment '是否已读',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    -- 索引
    INDEX idx_toUserId (toUserId),
    INDEX idx_fromUserId (fromUserId),
    INDEX idx_type (type),
    INDEX idx_isRead (isRead),
    INDEX idx_createTime (createTime)
) comment '用户消息' collate = utf8mb4_unicode_ci;

-- 系统配置表 (新增)
CREATE TABLE IF NOT EXISTS system_config
(
    id          bigint auto_increment comment 'ID' primary key,
    configKey   varchar(100)                           not null comment '配置键',
    configValue text                                   null comment '配置值',
    description varchar(500)                           null comment '配置描述',
    type        varchar(50)  default 'string'          not null comment '配置类型',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    -- 索引
    UNIQUE KEY uk_configKey (configKey)
) comment '系统配置' collate = utf8mb4_unicode_ci;
