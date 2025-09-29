-- ========================================
-- Math Platform 完整数据库初始化脚本
-- 版本: v2.0
-- 说明: 按顺序执行所有模块的SQL脚本
-- 使用方法: mysql -u root -p < 00_init_all.sql
-- ========================================

-- 执行顺序说明：
-- 1. 数据库初始化
-- 2. 用户管理相关表
-- 3. 内容管理相关表  
-- 4. 社交互动相关表
-- 5. 系统管理相关表
-- 6. 壁纸功能相关表
-- 7. 基础数据初始化
-- 8. 演示数据初始化（可选）

SOURCE 01_database_init.sql;
SOURCE 02_user_tables.sql;
SOURCE 03_content_tables.sql;
SOURCE 04_social_tables.sql;
SOURCE 05_system_tables.sql;
SOURCE 06_wallpaper_tables.sql;
SOURCE 07_basic_data.sql;
SOURCE 08_demo_data.sql;
