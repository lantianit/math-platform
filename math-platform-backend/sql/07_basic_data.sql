-- ========================================
-- Math Platform 基础数据初始化
-- 版本: v2.0
-- 功能: 初始化分类、标签、系统配置、管理员账户等基础数据
-- ========================================

USE mathplatform;

-- ============== 初始化基础数据（幂等） ==============

-- 初始化分类数据
INSERT IGNORE INTO category (name, description, icon, color, sort) VALUES
('技术讨论', '分享技术心得、讨论技术问题', 'CodeOutlined', '#1890ff', 1),
('问题求助', '遇到问题时寻求帮助', 'QuestionCircleOutlined', '#52c41a', 2),
('项目展示', '展示个人或团队项目', 'ProjectOutlined', '#722ed1', 3),
('经验分享', '分享学习和工作经验', 'ShareAltOutlined', '#fa8c16', 4),
('资源推荐', '推荐优质学习资源', 'StarOutlined', '#eb2f96', 5),
('闲聊灌水', '日常交流和闲聊', 'CommentOutlined', '#13c2c2', 6);

-- 初始化标签数据
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

-- 初始化系统配置
INSERT IGNORE INTO system_config (config_key, config_value, type, description) VALUES
('site.name', 'Math Platform', 'string', '网站名称'),
('site.description', '企业级数学论坛平台', 'string', '网站描述'),
('site.keywords', '数学,论坛,技术交流,学习', 'string', '网站关键词'),
('post.max_length', '10000', 'number', '帖子最大长度'),
('comment.max_length', '1000', 'number', '评论最大长度'),
('upload.max_size', '10485760', 'number', '上传文件最大大小(字节)'),
('user.default_avatar', '/images/default-avatar.png', 'string', '默认头像'),
('notification.enabled', 'true', 'boolean', '是否启用通知功能');

-- 初始化管理员账号（幂等）
INSERT INTO user (user_account, user_password, user_name, user_avatar, user_profile, user_role, status)
SELECT * FROM (
  SELECT 'admin'    AS user_account, 
         'b0dd3697a192885d7c055db46155b26a' AS user_password, 
         '系统管理员' AS user_name, 
         '/images/avatars/admin.png' AS user_avatar, 
         '数学平台系统管理员，负责平台运营和管理工作。' AS user_profile, 
         'admin' AS user_role, 
         1 AS status
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM user WHERE user_account = tmp.user_account);
