-- =============================================
-- 创新功能扩展脚本
-- 适用范围：在现有项目数据库基础上增量补充
-- 功能覆盖：行为日志、勋章体系
-- 说明：项目使用 JPA update 也会自动建表，这份脚本用于手动同步或答辩展示
-- =============================================

USE bishe;

CREATE TABLE IF NOT EXISTS user_behavior_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    behavior_type VARCHAR(50) NOT NULL COMMENT '行为类型',
    game_id BIGINT NULL COMMENT '关联游戏ID',
    reference_id BIGINT NULL COMMENT '关联业务ID，如帖子ID/订单ID',
    detail VARCHAR(255) NULL COMMENT '补充说明',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_behavior_user_id (user_id),
    INDEX idx_behavior_game_id (game_id),
    INDEX idx_behavior_type (behavior_type),
    INDEX idx_behavior_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为日志表';

CREATE TABLE IF NOT EXISTS user_badges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    code VARCHAR(50) NOT NULL COMMENT '勋章编码',
    name VARCHAR(100) NOT NULL COMMENT '勋章名称',
    description VARCHAR(255) NOT NULL COMMENT '勋章说明',
    earned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    UNIQUE KEY uk_user_badges_user_code (user_id, code),
    INDEX idx_user_badges_user_id (user_id),
    INDEX idx_user_badges_earned_at (earned_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户勋章表';

-- 可选：为后续推荐与分析演示插入几条行为日志样例
-- INSERT INTO user_behavior_logs (user_id, behavior_type, game_id, reference_id, detail)
-- VALUES
-- (1, 'VIEW_GAME', 1, NULL, '浏览游戏详情'),
-- (1, 'ADD_TO_CART', 2, NULL, '加入购物车'),
-- (1, 'CREATE_FORUM_POST', 1, 1, '发布攻略讨论');
