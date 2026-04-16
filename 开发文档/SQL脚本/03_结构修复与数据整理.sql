-- 游戏社区商城系统
-- 结构修复与数据整理脚本
-- 适用场景：从旧环境迁移到当前结构，或历史数据存在字段不一致时

USE bishe;

-- 1. 统一 games 表字段格式
ALTER TABLE games
    MODIFY COLUMN gallery TEXT NULL,
    MODIFY COLUMN system_requirements TEXT NULL,
    MODIFY COLUMN tags VARCHAR(200) NULL;

-- 2. categories 状态兜底
UPDATE categories
SET status = 'ACTIVE'
WHERE status IS NULL OR status = '';

-- 2.1 users.points 字段补齐
SET @sql_add_users_points = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'users'
              AND COLUMN_NAME = 'points'
        ),
        'SELECT ''users.points already exists''',
        'ALTER TABLE users ADD COLUMN points INT NOT NULL DEFAULT 0 AFTER avatar'
    )
);
PREPARE stmt FROM @sql_add_users_points;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 补齐 posts.game_id
SET @sql_add_posts_game_id = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'posts'
              AND COLUMN_NAME = 'game_id'
        ),
        'SELECT ''posts.game_id already exists''',
        'ALTER TABLE posts ADD COLUMN game_id BIGINT NULL AFTER user_id'
    )
);
PREPARE stmt FROM @sql_add_posts_game_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4. 补齐 posts.last_comment_at
SET @sql_add_posts_last_comment_at = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'posts'
              AND COLUMN_NAME = 'last_comment_at'
        ),
        'SELECT ''posts.last_comment_at already exists''',
        'ALTER TABLE posts ADD COLUMN last_comment_at DATETIME NULL AFTER updated_at'
    )
);
PREPARE stmt FROM @sql_add_posts_last_comment_at;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5. posts.game_id 索引
SET @sql_add_idx_posts_game = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'posts'
              AND INDEX_NAME = 'idx_posts_game_id'
        ),
        'SELECT ''idx_posts_game_id already exists''',
        'ALTER TABLE posts ADD INDEX idx_posts_game_id (game_id)'
    )
);
PREPARE stmt FROM @sql_add_idx_posts_game;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 6. posts.last_comment_at 索引
SET @sql_add_idx_posts_last_comment = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'posts'
              AND INDEX_NAME = 'idx_posts_last_comment_at'
        ),
        'SELECT ''idx_posts_last_comment_at already exists''',
        'ALTER TABLE posts ADD INDEX idx_posts_last_comment_at (last_comment_at)'
    )
);
PREPARE stmt FROM @sql_add_idx_posts_last_comment;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 7. posts.game_id 外键
SET @sql_add_fk_posts_game = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'posts'
              AND CONSTRAINT_NAME = 'fk_posts_game'
        ),
        'SELECT ''fk_posts_game already exists''',
        'ALTER TABLE posts ADD CONSTRAINT fk_posts_game FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE SET NULL'
    )
);
PREPARE stmt FROM @sql_add_fk_posts_game;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 8. 补齐多分类关联表
CREATE TABLE IF NOT EXISTS game_categories (
    game_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (game_id, category_id),
    INDEX idx_gc_category_id (category_id),
    CONSTRAINT fk_gc_game
        FOREIGN KEY (game_id) REFERENCES games(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_gc_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏与分类关联表';

-- 8.1 购物车表
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    selected BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_cart_items_user_game (user_id, game_id),
    INDEX idx_cart_items_user_id (user_id),
    INDEX idx_cart_items_game_id (game_id),
    INDEX idx_cart_items_selected (selected),
    CONSTRAINT fk_cart_items_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_game
        FOREIGN KEY (game_id) REFERENCES games(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 8.2 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(40) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    payable_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    points_earned INT NOT NULL DEFAULT 0,
    status ENUM('PENDING', 'PAID', 'CANCELLED') NOT NULL DEFAULT 'PAID',
    payment_method ENUM('MOCK', 'FREE') NOT NULL DEFAULT 'MOCK',
    paid_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 8.3 订单项表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    game_name VARCHAR(100) NOT NULL,
    game_image_url VARCHAR(500) NULL,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    quantity INT NOT NULL DEFAULT 1,
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_items_order_id (order_id),
    INDEX idx_order_items_game_id (game_id),
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_order_items_game
        FOREIGN KEY (game_id) REFERENCES games(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 8.4 用户游戏库表
CREATE TABLE IF NOT EXISTS user_games (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    order_id BIGINT NULL,
    acquired_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    acquired_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_played_at DATETIME NULL,
    UNIQUE KEY uk_user_games_user_game (user_id, game_id),
    INDEX idx_user_games_user_id (user_id),
    INDEX idx_user_games_game_id (game_id),
    CONSTRAINT fk_user_games_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_games_game
        FOREIGN KEY (game_id) REFERENCES games(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_games_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户游戏库表';

-- 8.5 积分流水表
CREATE TABLE IF NOT EXISTS point_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    change_amount INT NOT NULL,
    balance_after INT NOT NULL,
    type ENUM('EARN', 'SPEND', 'ADJUST') NOT NULL,
    description VARCHAR(255) NOT NULL,
    order_id BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_point_transactions_user_id (user_id),
    INDEX idx_point_transactions_order_id (order_id),
    INDEX idx_point_transactions_created_at (created_at),
    CONSTRAINT fk_point_transactions_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_point_transactions_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分流水表';

-- 9. 从旧字段回填多分类关系
INSERT INTO game_categories (game_id, category_id)
SELECT g.id, g.category_id
FROM games g
WHERE g.category_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM game_categories gc
      WHERE gc.game_id = g.id
        AND gc.category_id = g.category_id
  );

-- 10. 标签从 JSON 数组格式转为逗号分隔字符串
UPDATE games
SET tags = REPLACE(
              REPLACE(
                  REPLACE(
                      REPLACE(tags, '["', ''),
                  '"]', ''),
              '","', ','),
          '"', '')
WHERE tags LIKE '[%';

-- 11. 评分归一化到 5 分制
UPDATE games
SET rating = ROUND(rating / 2, 2)
WHERE rating > 5;

-- 12. 为空的轮播图链接兜底
UPDATE banners
SET link_url = '/#featured'
WHERE link_url IS NULL OR TRIM(link_url) = '';

-- 13. 诊断信息：重复分类
SELECT 'duplicate_categories' AS check_name, name, COUNT(*) AS total
FROM categories
GROUP BY name
HAVING COUNT(*) > 1;

-- 14. 诊断信息：重复游戏
SELECT 'duplicate_games' AS check_name, name, COUNT(*) AS total
FROM games
GROUP BY name
HAVING COUNT(*) > 1;
