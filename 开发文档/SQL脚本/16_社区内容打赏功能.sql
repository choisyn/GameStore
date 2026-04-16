-- 社区内容打赏功能
-- 支持“讨论广场帖子”和“游戏攻略”的积分打赏
USE bishe;

CREATE TABLE IF NOT EXISTS content_rewards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    target_type ENUM('FORUM_POST', 'GAME_GUIDE') NOT NULL,
    target_id BIGINT NOT NULL,
    giver_user_id BIGINT NOT NULL,
    receiver_user_id BIGINT NOT NULL,
    points_amount INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_content_rewards_target (target_type, target_id),
    INDEX idx_content_rewards_giver (giver_user_id),
    INDEX idx_content_rewards_receiver (receiver_user_id),
    INDEX idx_content_rewards_created_at (created_at),
    CONSTRAINT fk_content_rewards_giver
        FOREIGN KEY (giver_user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_content_rewards_receiver
        FOREIGN KEY (receiver_user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容打赏记录表';

-- 可选检查
-- SELECT * FROM content_rewards ORDER BY id DESC;
