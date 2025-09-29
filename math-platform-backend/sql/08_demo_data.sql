-- ========================================
-- Math Platform 演示数据初始化
-- 版本: v2.0
-- 功能: 初始化演示用户、帖子、评论、互动等数据
-- ========================================

USE mathplatform;

-- ============== 示例数据：用户、帖子与互动 ==============

-- 初始化演示用户（幂等）
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
