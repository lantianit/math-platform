-- ========================================
-- Math Platform 论坛测试数据
-- ========================================

USE mathplatform;

-- 清理现有测试数据（可选）
-- DELETE FROM user_follow WHERE id > 0;
-- DELETE FROM post_favourite WHERE id > 0;
-- DELETE FROM post_like WHERE id > 0;
-- DELETE FROM comment WHERE id > 0;
-- DELETE FROM post WHERE id > 0;
-- DELETE FROM user WHERE id > 0;

-- 插入测试用户数据
INSERT INTO user (id, userAccount, userPassword, userName, userAvatar, userProfile, userRole, gender, email, phone, location, followingCount, followerCount, postCount, likeCount, status) VALUES
(1, 'admin', '$2a$10$3JqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ', '系统管理员', 'https://avatars.githubusercontent.com/u/1?v=4', '系统管理员账号', 'admin', 1, 'admin@mathplatform.com', '18800000001', '北京市', 5, 100, 10, 500, 1),
(2, 'zhangsan', '$2a$10$3JqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ', '张三', 'https://avatars.githubusercontent.com/u/2?v=4', '数学爱好者，专注于高等数学研究', 'user', 1, 'zhangsan@example.com', '18800000002', '上海市', 10, 50, 25, 200, 1),
(3, 'lisi', '$2a$10$3JqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ', '李四', 'https://avatars.githubusercontent.com/u/3?v=4', '线性代数专家，喜欢分享学习心得', 'user', 2, 'lisi@example.com', '18800000003', '广州市', 8, 35, 18, 150, 1),
(4, 'wangwu', '$2a$10$3JqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ', '王五', 'https://avatars.githubusercontent.com/u/4?v=4', '概率论与数理统计研究者', 'user', 1, 'wangwu@example.com', '18800000004', '深圳市', 12, 80, 30, 300, 1),
(5, 'zhaoliu', '$2a$10$3JqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ', '赵六', 'https://avatars.githubusercontent.com/u/5?v=4', '离散数学爱好者', 'user', 2, 'zhaoliu@example.com', '18800000005', '杭州市', 6, 25, 12, 80, 1),
(6, 'sunqi', '$2a$10$3JqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ', '孙七', 'https://avatars.githubusercontent.com/u/6?v=4', '数学建模竞赛选手', 'user', 1, 'sunqi@example.com', '18800000006', '成都市', 15, 60, 22, 180, 1),
(7, 'zhouba', '$2a$10$3JqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ', '周八', 'https://avatars.githubusercontent.com/u/7?v=4', '微积分学习者', 'user', 2, 'zhouba@example.com', '18800000007', '西安市', 4, 20, 8, 60, 1),
(8, 'wujiu', '$2a$10$3JqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ', '吴九', 'https://avatars.githubusercontent.com/u/8?v=4', '数学教师，热爱教学', 'user', 1, 'wujiu@example.com', '18800000008', '南京市', 20, 120, 45, 400, 1),
(9, 'zhengshi', '$2a$10$3JqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ', '郑十', 'https://avatars.githubusercontent.com/u/9?v=4', '应用数学研究生', 'user', 2, 'zhengshi@example.com', '18800000009', '武汉市', 7, 30, 15, 120, 1),
(10, 'liuyi', '$2a$10$3JqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJqJ', '刘一', 'https://avatars.githubusercontent.com/u/10?v=4', '数学竞赛教练', 'user', 1, 'liuyi@example.com', '18800000010', '天津市', 25, 150, 60, 500, 1);

-- 插入测试帖子数据
INSERT INTO post (id, title, content, contentSummary, userId, category, tags, viewCount, likeCount, commentCount, favouriteCount, qualityScore, hotScore, status, isTop, isHot) VALUES
(1, '高等数学学习方法分享', '作为一个数学专业的学生，我想分享一些学习高等数学的心得体会。首先，理解概念比记忆公式更重要...', '分享高等数学学习方法和心得体会', 2, 'share', '["高等数学", "学习方法", "心得分享"]', 1250, 85, 23, 45, 4.5, 89.5, 1, 1, 1),
(2, '线性代数中矩阵运算的几何意义', '很多同学在学习线性代数时，只会机械地进行矩阵运算，却不理解其几何意义。本文将通过图解的方式...', '解释线性代数矩阵运算的几何意义', 3, 'tech', '["线性代数", "矩阵运算", "几何意义"]', 980, 67, 18, 32, 4.2, 78.3, 1, 0, 1),
(3, '概率论中的贝叶斯定理应用实例', '贝叶斯定理是概率论中的重要定理，在实际生活中有广泛应用。今天我们通过几个具体例子来理解...', '通过实例讲解贝叶斯定理的应用', 4, 'tech', '["概率论", "贝叶斯定理", "应用实例"]', 1100, 72, 15, 28, 4.3, 82.1, 1, 0, 1),
(4, '求助：这道微积分题怎么解？', '各位大神，我遇到一道微积分题，试了很多方法都解不出来，求助！题目如下：∫(x²+1)/(x³+3x+2)dx', '求助解决一道微积分积分题', 7, 'question', '["微积分", "积分", "求助"]', 650, 12, 8, 5, 3.1, 25.8, 1, 0, 0),
(5, '数学建模竞赛经验分享', '刚参加完全国大学生数学建模竞赛，获得了省一等奖，想和大家分享一些参赛经验和心得...', '分享数学建模竞赛的参赛经验', 6, 'share', '["数学建模", "竞赛经验", "心得分享"]', 890, 56, 12, 22, 4.0, 65.2, 1, 0, 0),
(6, '离散数学：图论基础知识总结', '图论是离散数学的重要分支，本文总结了图论的基础知识点，包括图的定义、图的表示方法...', '总结离散数学图论的基础知识', 5, 'tech', '["离散数学", "图论", "基础知识"]', 720, 38, 9, 15, 3.8, 52.4, 1, 0, 0),
(7, '微积分学习资源推荐', '整理了一些优质的微积分学习资源，包括教材、视频课程、练习题等，希望对大家有帮助...', '推荐优质的微积分学习资源', 8, 'share', '["微积分", "学习资源", "推荐"]', 1350, 95, 28, 52, 4.6, 92.8, 1, 0, 1),
(8, '数学教学中的常见误区', '作为一名数学教师，我发现学生在学习过程中经常会陷入一些误区，今天总结几个最常见的...', '总结数学教学中学生常犯的错误', 8, 'share', '["数学教学", "常见误区", "教学经验"]', 680, 42, 11, 18, 3.9, 58.7, 1, 0, 0),
(9, '应用数学专业就业前景分析', '作为应用数学专业的研究生，想和大家分享一下这个专业的就业前景和发展方向...', '分析应用数学专业的就业前景', 9, 'share', '["应用数学", "就业前景", "专业分析"]', 1080, 63, 19, 35, 4.1, 74.5, 1, 0, 0),
(10, '数学竞赛训练方法与技巧', '多年的竞赛教练经验让我总结出了一套有效的训练方法，今天分享给有志于参加数学竞赛的同学们...', '分享数学竞赛的训练方法和技巧', 10, 'share', '["数学竞赛", "训练方法", "竞赛技巧"]', 950, 78, 16, 29, 4.4, 81.2, 1, 0, 1),
(11, '求解这道线性代数题', '这道关于特征值和特征向量的题目困扰了我很久，希望有高手能帮忙解答一下...', '求助解决线性代数特征值问题', 5, 'question', '["线性代数", "特征值", "求助"]', 420, 8, 6, 3, 2.8, 18.3, 1, 0, 0),
(12, '概率统计在机器学习中的应用', '随着AI的发展，概率统计在机器学习中扮演着重要角色，本文介绍几个核心概念...', '介绍概率统计在机器学习中的应用', 4, 'tech', '["概率统计", "机器学习", "应用"]', 1200, 89, 21, 41, 4.5, 88.7, 1, 0, 1);

-- 插入测试评论数据
INSERT INTO comment (id, postId, userId, content, parentId, rootId, replyToId, likeCount, status) VALUES
(1, 1, 3, '写得非常好！我也是这样学习高等数学的，特别是你提到的理解概念这一点，深有同感。', NULL, NULL, NULL, 15, 1),
(2, 1, 4, '感谢分享，对我很有帮助。请问你有推荐的高等数学教材吗？', NULL, NULL, NULL, 8, 1),
(3, 1, 2, '推荐同济版的《高等数学》，讲解很详细，例题也很丰富。', 2, 2, 4, 12, 1),
(4, 1, 5, '我觉得还可以配合一些视频课程，比如网易公开课上的MIT课程。', NULL, NULL, NULL, 6, 1),
(5, 2, 6, '矩阵的几何意义确实很重要，很多人只会计算不会理解。', NULL, NULL, NULL, 9, 1),
(6, 2, 7, '能否详细讲解一下矩阵乘法的几何意义？', NULL, NULL, NULL, 4, 1),
(7, 2, 3, '矩阵乘法可以看作是线性变换的复合，每个矩阵代表一种变换。', 6, 6, 7, 11, 1),
(8, 3, 8, '贝叶斯定理在医学诊断中应用很广泛，这个例子很好！', NULL, NULL, NULL, 7, 1),
(9, 4, 2, '这道题可以用部分分式分解的方法，先将分母因式分解。', NULL, NULL, NULL, 5, 1),
(10, 4, 3, '我也遇到过类似的题目，关键是要找到合适的换元方法。', NULL, NULL, NULL, 3, 1),
(11, 5, 9, '恭喜获奖！请问在建模过程中如何选择合适的数学模型？', NULL, NULL, NULL, 8, 1),
(12, 5, 6, '模型选择要根据实际问题的特点，没有万能的模型，需要多尝试。', 11, 11, 9, 6, 1),
(13, 7, 10, '资源很全面，特别是那个MIT的公开课，讲得真的很好。', NULL, NULL, NULL, 12, 1),
(14, 7, 1, '建议大家结合教材和视频一起学习，效果更好。', NULL, NULL, NULL, 8, 1),
(15, 8, 5, '作为学生，我确实犯过这些错误，老师总结得很到位。', NULL, NULL, NULL, 7, 1);

-- 插入测试点赞数据
INSERT INTO post_like (postId, userId, type) VALUES
(1, 3, 1), (1, 4, 1), (1, 5, 1), (1, 6, 1), (1, 7, 1), (1, 8, 1), (1, 9, 1), (1, 10, 1),
(2, 4, 1), (2, 5, 1), (2, 6, 1), (2, 7, 1), (2, 8, 1),
(3, 2, 1), (3, 5, 1), (3, 6, 1), (3, 7, 1), (3, 8, 1), (3, 9, 1),
(7, 2, 1), (7, 3, 1), (7, 4, 1), (7, 5, 1), (7, 6, 1), (7, 9, 1), (7, 10, 1),
(10, 2, 1), (10, 3, 1), (10, 4, 1), (10, 5, 1), (10, 6, 1),
(12, 2, 1), (12, 3, 1), (12, 5, 1), (12, 6, 1), (12, 7, 1), (12, 8, 1);

-- 插入测试收藏数据
INSERT INTO post_favourite (postId, userId) VALUES
(1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(2, 4), (2, 5), (2, 6),
(3, 2), (3, 5), (3, 6),
(7, 2), (7, 3), (7, 4), (7, 5), (7, 9),
(10, 2), (10, 3), (10, 4),
(12, 2), (12, 3), (12, 5), (12, 6);

-- 插入测试关注数据
INSERT INTO user_follow (followerId, followingId) VALUES
(2, 3), (2, 4), (2, 8), (2, 10),  -- 张三关注李四、王五、吴九、刘一
(3, 2), (3, 4), (3, 6),           -- 李四关注张三、王五、孙七
(4, 2), (4, 3), (4, 8), (4, 10),  -- 王五关注张三、李四、吴九、刘一
(5, 2), (5, 8),                   -- 赵六关注张三、吴九
(6, 2), (6, 4), (6, 8), (6, 10),  -- 孙七关注张三、王五、吴九、刘一
(7, 2), (7, 8),                   -- 周八关注张三、吴九
(8, 2), (8, 3), (8, 4), (8, 6), (8, 10), -- 吴九关注多人
(9, 2), (9, 4), (9, 8),           -- 郑十关注张三、王五、吴九
(10, 2), (10, 3), (10, 4), (10, 6), (10, 8); -- 刘一关注多人

-- 更新用户统计数据（关注数、粉丝数等）
UPDATE user SET 
    followingCount = (SELECT COUNT(*) FROM user_follow WHERE followerId = user.id),
    followerCount = (SELECT COUNT(*) FROM user_follow WHERE followingId = user.id),
    postCount = (SELECT COUNT(*) FROM post WHERE userId = user.id AND status = 1),
    likeCount = (SELECT COALESCE(SUM(p.likeCount), 0) FROM post p WHERE p.userId = user.id AND p.status = 1)
WHERE id > 0;

-- 插入系统配置数据
INSERT INTO system_config (configKey, configValue, description, type) VALUES
('site.name', 'Math Platform', '网站名称', 'string'),
('site.description', '专业的数学学习交流平台', '网站描述', 'string'),
('post.max_title_length', '200', '帖子标题最大长度', 'number'),
('post.max_content_length', '10000', '帖子内容最大长度', 'number'),
('comment.max_length', '1000', '评论最大长度', 'number'),
('upload.max_file_size', '10485760', '文件上传最大大小（字节）', 'number'),
('upload.allowed_types', '["jpg","jpeg","png","gif","pdf","doc","docx"]', '允许上传的文件类型', 'json'),
('hot.post.view_weight', '0.3', '热门帖子浏览量权重', 'number'),
('hot.post.like_weight', '0.4', '热门帖子点赞数权重', 'number'),
('hot.post.comment_weight', '0.3', '热门帖子评论数权重', 'number');
