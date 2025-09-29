-- ========================================
-- Math Platform 数据库初始化脚本
-- 版本: v2.0
-- 功能: 创建数据库和基础配置
-- ========================================

-- 建库与选库
CREATE DATABASE IF NOT EXISTS mathplatform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mathplatform;

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
