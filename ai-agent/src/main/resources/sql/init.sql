-- 创建AI模块数据表

-- 用户画像表
CREATE TABLE IF NOT EXISTS `ai_user_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `learning_history` text COMMENT '学习历史',
  `interest_tags` varchar(500) COMMENT '兴趣标签，逗号分隔',
  `learning_goal` varchar(500) COMMENT '学习目标',
  `learning_style` varchar(200) COMMENT '学习风格',
  `learning_rhythm` varchar(200) COMMENT '学习节奏',
  `potential_needs` varchar(500) COMMENT '潜在需求',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像表';

-- 课程推荐表
CREATE TABLE IF NOT EXISTS `ai_recommendation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `recommendation_data` text COMMENT '推荐数据',
  `recommendation_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '推荐时间',
  `feedback` varchar(500) COMMENT '用户反馈',
  `feedback_time` datetime COMMENT '反馈时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程推荐表';

-- 管理端查询历史表
CREATE TABLE IF NOT EXISTS `ai_admin_query` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `admin_id` bigint(20) NOT NULL COMMENT '管理员ID',
  `platform_data` text COMMENT '平台数据',
  `admin_question` text COMMENT '管理员问题',
  `ai_response` text COMMENT 'AI响应',
  `query_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '查询时间',
  PRIMARY KEY (`id`),
  KEY `idx_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理端查询历史表';

-- 向量存储表（用于后续扩展）
CREATE TABLE IF NOT EXISTS `ai_vector_store` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` varchar(50) COMMENT '内容类型',
  `vector_data` text COMMENT '向量数据',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='向量存储表';
