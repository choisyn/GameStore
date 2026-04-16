-- 游戏攻略系统
-- 说明：为商城新增一个简单的“游戏攻略”内容表
USE bishe;

CREATE TABLE IF NOT EXISTS game_guides (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    game_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500) NULL,
    content LONGTEXT NOT NULL,
    cover_image_url VARCHAR(500) NULL,
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') NOT NULL DEFAULT 'BEGINNER',
    estimated_minutes INT NOT NULL DEFAULT 15,
    tags VARCHAR(255) NULL,
    view_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    status ENUM('PUBLISHED', 'DRAFT', 'ARCHIVED') NOT NULL DEFAULT 'PUBLISHED',
    published_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_game_guides_game_id (game_id),
    INDEX idx_game_guides_author_id (author_id),
    INDEX idx_game_guides_status (status),
    INDEX idx_game_guides_featured (is_featured),
    INDEX idx_game_guides_published_at (published_at),
    CONSTRAINT fk_game_guides_game
        FOREIGN KEY (game_id) REFERENCES games(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_game_guides_author
        FOREIGN KEY (author_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏攻略表';

ALTER TABLE game_guides
    MODIFY COLUMN published_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 可选检查
-- SELECT * FROM game_guides ORDER BY id DESC;
