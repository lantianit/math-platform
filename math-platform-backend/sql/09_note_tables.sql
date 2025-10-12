-- 笔记空间表
CREATE TABLE IF NOT EXISTS note_space (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '空间ID',
    space_name VARCHAR(128) NOT NULL COMMENT '空间名称',
    space_level INT DEFAULT 0 NOT NULL COMMENT '空间级别：0-普通版 1-专业版 2-旗舰版',
    max_note_count BIGINT DEFAULT 100 NOT NULL COMMENT '最大笔记数量',
    total_note_count BIGINT DEFAULT 0 NOT NULL COMMENT '当前笔记数量',
    max_size BIGINT DEFAULT 104857600 NOT NULL COMMENT '最大存储大小（字节）',
    total_size BIGINT DEFAULT 0 NOT NULL COMMENT '当前使用大小（字节）',
    user_id BIGINT NOT NULL COMMENT '创建用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    edit_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '编辑时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX idx_user_id (user_id),
    INDEX idx_space_name (space_name),
    INDEX idx_space_level (space_level),
    INDEX idx_is_delete (is_delete)
) COMMENT '笔记空间表' COLLATE = utf8mb4_unicode_ci;

-- 笔记表
CREATE TABLE IF NOT EXISTS note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '笔记ID',
    title VARCHAR(200) NOT NULL COMMENT '笔记标题',
    content TEXT NOT NULL COMMENT '笔记内容（Markdown格式）',
    content_summary VARCHAR(500) NULL COMMENT '内容摘要',
    user_id BIGINT NOT NULL COMMENT '创建者ID',
    space_id BIGINT NULL COMMENT '空间ID（为空表示公共笔记）',
    category VARCHAR(50) DEFAULT 'study' NOT NULL COMMENT '分类',
    tags VARCHAR(1000) NULL COMMENT '标签，JSON格式',
    attachments VARCHAR(2000) NULL COMMENT '附件URL，JSON格式',
    attachment_size BIGINT DEFAULT 0 NOT NULL COMMENT '附件总大小（字节）',
    view_count INT DEFAULT 0 NOT NULL COMMENT '浏览量',
    like_count INT DEFAULT 0 NOT NULL COMMENT '点赞数',
    favourite_count INT DEFAULT 0 NOT NULL COMMENT '收藏数',
    status TINYINT DEFAULT 1 NOT NULL COMMENT '状态：0-草稿 1-已发布 2-审核中',
    audit_status TINYINT DEFAULT 1 NOT NULL COMMENT '审核状态：0-待审核 1-通过 2-拒绝',
    audit_time DATETIME NULL COMMENT '审核时间',
    audit_user_id BIGINT NULL COMMENT '审核人ID',
    publish_time DATETIME NULL COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    edit_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '编辑时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除',
    INDEX idx_user_id (user_id),
    INDEX idx_space_id (space_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_is_delete (is_delete),
    FULLTEXT INDEX ft_title_content (title, content)
) COMMENT '笔记表' COLLATE = utf8mb4_unicode_ci;

