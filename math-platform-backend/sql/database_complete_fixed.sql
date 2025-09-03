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
    userRole       varchar(50)  default 'user'            not null comment '用户角色：user/admin/ban',
    gender         tinyint      default 0                 not null comment '性别：0-未知 1-男 2-女',
    email          varchar(100)                           null comment '邮箱',
    phone          varchar(20)                            null comment '手机号',
    birthday       datetime                               null comment '生日',  -- 新增：修复ALTER时缺失的字段
    location       varchar(100)                           null comment '所在地',-- 新增：修复ALTER时缺失的字段
    website        varchar(200)                           null comment '个人网站',-- 新增：修复ALTER时缺失的字段
-- 统一字段名：与后续逻辑保持一致
    followingCount int          default 0                 not null comment '关注数',
    followerCount  int          default 0                 not null comment '粉丝数',
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

-- 帖子表（修复tags字段格式，支持JSON）
CREATE TABLE IF NOT EXISTS post
(
    id             bigint auto_increment comment '帖子ID' primary key,
    title          varchar(200)                           not null comment '帖子标题',
    content        text                                   not null comment '帖子内容',
    contentSummary varchar(500)                           null comment '内容摘要',
    userId         bigint                                 not null comment '发布者ID',
    category       varchar(50)  default 'tech'            not null comment '分类：tech/question/project/share',
    tags           json                                   null comment '标签（JSON格式，显式指定类型）',-- 修复：从varchar改为json
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

-- 以下表结构无错误，保持原逻辑
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
-- 第二部分: 移除冗余的字段顺序修复（关键修复）
-- ========================================
-- 原脚本中“字段顺序修复”逻辑存在2个问题：
-- 1. 引用未定义字段导致ALTER失败；2. 字段顺序仅影响显示，无业务意义
-- 因此直接移除该部分冗余逻辑，避免执行报错

-- ========================================
-- 第三部分: 基础数据初始化（保持原逻辑，无错误）
-- ========================================

-- 插入默认分类（使用INSERT IGNORE避免重复插入）
INSERT IGNORE INTO category (name, description, icon, color, sort) VALUES
                                                                       ('技术讨论', '分享技术心得、讨论技术问题', 'CodeOutlined', '#1890ff', 1),
                                                                       ('问题求助', '遇到问题时寻求帮助', 'QuestionCircleOutlined', '#52c41a', 2),
                                                                       ('项目展示', '展示个人或团队项目', 'ProjectOutlined', '#722ed1', 3),
                                                                       ('经验分享', '分享学习和工作经验', 'ShareAltOutlined', '#fa8c16', 4),
                                                                       ('资源推荐', '推荐优质学习资源', 'StarOutlined', '#eb2f96', 5),
                                                                       ('闲聊灌水', '日常交流和闲聊', 'CommentOutlined', '#13c2c2', 6);

-- 插入默认标签（使用INSERT IGNORE避免重复插入）
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

-- 插入系统配置（使用INSERT IGNORE避免重复插入）
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
-- 第四部分: 测试数据插入（修复JSON格式依赖）
-- ========================================

-- 检查是否需要插入测试用户（避免重复）
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

-- 插入测试帖子数据（修复tags字段JSON格式：从字符串改为JSON对象）
INSERT INTO post (title, content, contentSummary, userId, category, tags, viewCount, likeCount, commentCount, favouriteCount, status)
SELECT * FROM (
                  SELECT
                      '深入理解微积分的本质：从极限到导数的数学之美' as title,
                      '# 微积分的本质探讨\n\n微积分作为现代数学的基础，其核心思想是通过极限来处理无穷小和无穷大的概念。\n\n## 极限的直观理解\n\n极限不是一个数值的到达，而是一个趋近的过程。当我们说 lim_{x→a} f(x) = L，我们实际上是在描述一个动态过程...\n\n## 导数的几何意义\n\n导数在几何上表示函数图像在某点的切线斜率。这个简单的概念却蕴含着深刻的数学思想...\n\n## 积分的物理意义\n\n积分可以理解为"累积"的概念，在物理学中有着广泛的应用...\n\n这些概念之间的联系构成了微积分的美妙体系。' as content,
                      '微积分作为现代数学的基础，通过极限处理无穷小概念，包含导数的几何意义和积分的物理意义。' as contentSummary,
                      (SELECT id FROM user WHERE userAccount = 'zhangsan' LIMIT 1) as userId,
    'tech' as category,
    JSON_ARRAY('微积分','导数','积分','数学分析') as tags,  -- 修复：使用JSON_ARRAY生成标准JSON
    1250 as viewCount, 45 as likeCount, 12 as commentCount, 28 as favouriteCount, 1 as status
UNION ALL
SELECT
    '线性代数在机器学习中的应用实例',
    '# 线性代数与机器学习\n\n线性代数是机器学习的数学基础，几乎所有的机器学习算法都离不开线性代数的支撑。\n\n## 向量和矩阵的基本概念\n\n在机器学习中，数据通常以向量或矩阵的形式表示...\n\n## 特征值和特征向量\n\n主成分分析(PCA)是降维的重要方法，其核心就是特征值分解...\n\n## 梯度下降算法\n\n梯度下降算法的数学原理基于向量微积分...\n\n通过具体的Python代码示例，我们可以看到这些数学概念是如何在实际项目中应用的。',
    '线性代数是机器学习的数学基础，包括向量矩阵、特征值分解、梯度下降等核心概念的应用。',
    (SELECT id FROM user WHERE userAccount = 'zhaoliu' LIMIT 1),
                      'tech',
                      JSON_ARRAY('线性代数','机器学习','PCA','梯度下降'),  -- 修复：标准JSON格式
                      980, 38, 15, 22, 1
UNION ALL
SELECT
    'React 18新特性深度解析：并发渲染的实现原理',
    '# React 18 并发特性详解\n\nReact 18引入了并发渲染，这是React架构的重大升级...\n\n## Concurrent Rendering\n\n并发渲染允许React在渲染过程中暂停和恢复，提供更好的用户体验...\n\n## Suspense的改进\n\n新版本的Suspense支持更多场景...\n\n## 自动批处理\n\nReact 18默认启用自动批处理...',
    'React 18引入并发渲染、改进Suspense、自动批处理等新特性，提升用户体验。',
    (SELECT id FROM user WHERE userAccount = 'wangwu' LIMIT 1),
                      'tech',
                      JSON_ARRAY('React','前端','并发渲染','JavaScript'),  -- 修复：标准JSON格式
                      756, 29, 8, 15, 1
UNION ALL
SELECT
    '求助：如何理解拉格朗日乘数法的几何意义？',
    '# 拉格朗日乘数法理解困难\n\n我在学习最优化理论时遇到了困难，特别是拉格朗日乘数法的几何意义。\n\n## 我的疑问\n\n1. 为什么梯度向量要平行？\n2. 拉格朗日乘数λ的几何意义是什么？\n3. 如何从直观上理解约束优化？\n\n## 我的理解\n\n目前我知道这是用来解决约束优化问题的方法，但是几何直观还是不太清楚...\n\n希望有经验的同学能够帮忙解答，最好能提供一些直观的例子。',
    '学习拉格朗日乘数法遇到困难，希望了解梯度平行、λ的几何意义等问题。',
    (SELECT id FROM user WHERE userAccount = 'liuyi' LIMIT 1),
                      'question',
                      JSON_ARRAY('拉格朗日乘数法','最优化','数学分析'),  -- 修复：标准JSON格式
                      423, 8, 11, 5, 1
              ) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM post LIMIT 1);  -- 仅当无帖子数据时插入

-- ========================================
-- 第五部分: 执行结果验证（新增：确认初始化成功）
-- ========================================

-- 显示完成信息
SELECT '✅ Math Platform 数据库初始化完成！' as message;

-- 显示核心表数据统计（验证数据插入结果）
SELECT
    (SELECT COUNT(*) FROM user) as 总用户数,
    (SELECT COUNT(*) FROM post) as 总帖子数,
    (SELECT COUNT(*) FROM comment) as 总评论数,
    (SELECT COUNT(*) FROM category) as 总分类数,
    (SELECT COUNT(*) FROM tag) as 总标签数,
    (SELECT COUNT(*) FROM system_config) as 总配置项数;

-- 验证管理员账号是否创建成功
SELECT
    IF(EXISTS(SELECT 1 FROM user WHERE userAccount = 'admin' AND userRole = 'admin'),
       '✅ 管理员账号创建成功（账号：admin，密码：123456）',  -- 注：密码b0dd3697a192885d7c055db46155b26a 对应明文123456
       '❌ 管理员账号创建失败') as 管理员账号验证结果;