-- 创建数据库和基础表结构

-- 创建数据库
CREATE DATABASE IF NOT EXISTS mathplatform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE mathplatform;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id            bigint auto_increment comment '帖子ID' primary key,
    title         varchar(200)                           not null comment '帖子标题',
    content       text                                   not null comment '帖子内容',
    userId        bigint                                 not null comment '发布者ID',
    category      varchar(50)  default 'tech'            not null comment '分类：tech/question/project',
    tags          varchar(500)                           null comment '标签，JSON格式',
    images        varchar(2000)                           null comment '图片URL，JSON格式',
    viewCount     int          default 0                 not null comment '浏览量',
    likeCount     int          default 0                 not null comment '点赞数',
    commentCount  int          default 0                 not null comment '评论数',
    favouriteCount int          default 0                 not null comment '收藏数',
    status        tinyint       default 1                 not null comment '状态：0-删除 1-正常',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    INDEX idx_userId (userId),
    INDEX idx_category (category),
    INDEX idx_createTime (createTime),
    INDEX idx_viewCount (viewCount)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 评论表
create table if not exists comment
(
    id         bigint auto_increment comment '评论ID' primary key,
    postId     bigint                                 not null comment '帖子ID',
    userId     bigint                                 not null comment '评论者ID',
    content    text                                   not null comment '评论内容',
    parentId   bigint       default null               null comment '父评论ID',
    likeCount  int          default 0                 not null comment '点赞数',
    status     tinyint       default 1                 not null comment '状态：0-删除 1-正常',
    createTime datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    INDEX idx_postId (postId),
    INDEX idx_userId (userId),
    INDEX idx_parentId (parentId)
) comment '评论' collate = utf8mb4_unicode_ci;

-- 帖子点赞表
create table if not exists post_like
(
    id         bigint auto_increment comment 'ID' primary key,
    postId     bigint                               not null comment '帖子ID',
    userId     bigint                               not null comment '用户ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    UNIQUE KEY uk_post_user (postId, userId),
    INDEX idx_userId (userId)
) comment '帖子点赞' collate = utf8mb4_unicode_ci;

-- 帖子收藏表
create table if not exists post_favourite
(
    id         bigint auto_increment comment 'ID' primary key,
    postId     bigint                               not null comment '帖子ID',
    userId     bigint                               not null comment '用户ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    UNIQUE KEY uk_post_user (postId, userId),
    INDEX idx_userId (userId)
) comment '帖子收藏' collate = utf8mb4_unicode_ci;

-- 用户关注表
create table if not exists user_follow
(
    id          bigint auto_increment comment 'ID' primary key,
    followerId  bigint                               not null comment '关注者ID',
    followingId bigint                               not null comment '被关注者ID',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    UNIQUE KEY uk_follower_following (followerId, followingId),
    INDEX idx_followerId (followerId),
    INDEX idx_followingId (followingId)
) comment '用户关注' collate = utf8mb4_unicode_ci;