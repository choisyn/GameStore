USE bishe;

-- =========================================================
-- 重复样本去重清理脚本
-- 适用场景：
-- 1. 样本脚本被重复执行，导致 games / posts / categories 出现重复
-- 2. 需要保留一条主记录，并把购物车、订单、帖子、行为日志等关联迁移过去
--
-- 执行前建议：
-- 1. 先备份数据库
-- 2. 可先手动查看以下重复情况：
--    SELECT name, COUNT(*) FROM categories GROUP BY name HAVING COUNT(*) > 1;
--    SELECT name, COUNT(*) FROM games GROUP BY name HAVING COUNT(*) > 1;
--    SELECT title, COUNT(*) FROM posts GROUP BY title HAVING COUNT(*) > 1;
-- =========================================================

START TRANSACTION;

-- ---------------------------------------------------------
-- 0. 先规范化名称，避免首尾空格导致看起来相同却未被识别
-- ---------------------------------------------------------
UPDATE categories
SET name = TRIM(name)
WHERE name IS NOT NULL;

UPDATE games
SET name = TRIM(name)
WHERE name IS NOT NULL;

UPDATE posts
SET title = TRIM(title)
WHERE title IS NOT NULL;

-- ---------------------------------------------------------
-- 1. 分类去重：按分类名保留最小 id
-- ---------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_duplicate_categories;
CREATE TEMPORARY TABLE tmp_duplicate_categories AS
SELECT
    c.id AS old_id,
    k.keep_id,
    c.name
FROM categories c
JOIN (
    SELECT TRIM(name) AS normalized_name, MIN(id) AS keep_id, COUNT(*) AS total_count
    FROM categories
    GROUP BY TRIM(name)
    HAVING COUNT(*) > 1
) k
    ON k.normalized_name = TRIM(c.name)
WHERE c.id <> k.keep_id;

DELETE gc
FROM game_categories gc
JOIN tmp_duplicate_categories d
    ON d.old_id = gc.category_id
JOIN game_categories existing_gc
    ON existing_gc.game_id = gc.game_id
   AND existing_gc.category_id = d.keep_id;

UPDATE game_categories gc
JOIN tmp_duplicate_categories d
    ON d.old_id = gc.category_id
SET gc.category_id = d.keep_id;

UPDATE games g
JOIN tmp_duplicate_categories d
    ON d.old_id = g.category_id
SET g.category_id = d.keep_id;

DELETE c
FROM categories c
JOIN tmp_duplicate_categories d
    ON d.old_id = c.id;

-- ---------------------------------------------------------
-- 2. 游戏去重：按游戏名保留最小 id
-- ---------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_duplicate_games;
CREATE TEMPORARY TABLE tmp_duplicate_games AS
SELECT
    g.id AS old_id,
    k.keep_id,
    g.name
FROM games g
JOIN (
    SELECT TRIM(name) AS normalized_name, MIN(id) AS keep_id, COUNT(*) AS total_count
    FROM games
    GROUP BY TRIM(name)
    HAVING COUNT(*) > 1
) k
    ON k.normalized_name = TRIM(g.name)
WHERE g.id <> k.keep_id;

-- 2.1 购物车：先合并冲突行，再迁移剩余引用
UPDATE cart_items keep_item
JOIN tmp_duplicate_games d
    ON d.keep_id = keep_item.game_id
JOIN cart_items old_item
    ON old_item.user_id = keep_item.user_id
   AND old_item.game_id = d.old_id
SET keep_item.selected = (keep_item.selected OR old_item.selected),
    keep_item.unit_price = LEAST(keep_item.unit_price, old_item.unit_price),
    keep_item.updated_at = GREATEST(keep_item.updated_at, old_item.updated_at);

DELETE old_item
FROM cart_items old_item
JOIN tmp_duplicate_games d
    ON d.old_id = old_item.game_id
JOIN cart_items keep_item
    ON keep_item.user_id = old_item.user_id
   AND keep_item.game_id = d.keep_id;

UPDATE cart_items old_item
JOIN tmp_duplicate_games d
    ON d.old_id = old_item.game_id
SET old_item.game_id = d.keep_id;

-- 2.2 用户游戏库：先合并冲突行，再迁移剩余引用
UPDATE user_games keep_game
JOIN tmp_duplicate_games d
    ON d.keep_id = keep_game.game_id
JOIN user_games old_game
    ON old_game.user_id = keep_game.user_id
   AND old_game.game_id = d.old_id
SET keep_game.order_id = COALESCE(keep_game.order_id, old_game.order_id),
    keep_game.acquired_price = LEAST(keep_game.acquired_price, old_game.acquired_price),
    keep_game.acquired_at = LEAST(keep_game.acquired_at, old_game.acquired_at),
    keep_game.last_played_at = CASE
        WHEN keep_game.last_played_at IS NULL THEN old_game.last_played_at
        WHEN old_game.last_played_at IS NULL THEN keep_game.last_played_at
        ELSE GREATEST(keep_game.last_played_at, old_game.last_played_at)
    END;

DELETE old_game
FROM user_games old_game
JOIN tmp_duplicate_games d
    ON d.old_id = old_game.game_id
JOIN user_games keep_game
    ON keep_game.user_id = old_game.user_id
   AND keep_game.game_id = d.keep_id;

UPDATE user_games old_game
JOIN tmp_duplicate_games d
    ON d.old_id = old_game.game_id
SET old_game.game_id = d.keep_id;

-- 2.3 游戏分类关联：先去掉将会冲突的映射，再迁移
DELETE gc
FROM game_categories gc
JOIN tmp_duplicate_games d
    ON d.old_id = gc.game_id
JOIN game_categories existing_gc
    ON existing_gc.game_id = d.keep_id
   AND existing_gc.category_id = gc.category_id;

UPDATE game_categories gc
JOIN tmp_duplicate_games d
    ON d.old_id = gc.game_id
SET gc.game_id = d.keep_id;

-- 2.4 其它引用迁移
UPDATE order_items oi
JOIN tmp_duplicate_games d
    ON d.old_id = oi.game_id
SET oi.game_id = d.keep_id;

UPDATE posts p
JOIN tmp_duplicate_games d
    ON d.old_id = p.game_id
SET p.game_id = d.keep_id;

UPDATE user_behavior_logs l
JOIN tmp_duplicate_games d
    ON d.old_id = l.game_id
SET l.game_id = d.keep_id;

UPDATE banners b
JOIN tmp_duplicate_games d
    ON b.link_url = CONCAT('/game/', d.old_id)
SET b.link_url = CONCAT('/game/', d.keep_id);

DELETE g
FROM games g
JOIN tmp_duplicate_games d
    ON d.old_id = g.id;

-- ---------------------------------------------------------
-- 3. 论坛帖子去重：按帖子标题保留最小 id
-- 说明：
-- 1. 只处理 discussion forum（posts 表）
-- 2. 用于清理重复导入的论坛样本标题
-- ---------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_duplicate_posts;
CREATE TEMPORARY TABLE tmp_duplicate_posts AS
SELECT
    p.id AS old_id,
    k.keep_id,
    p.title
FROM posts p
JOIN (
    SELECT TRIM(title) AS normalized_title, MIN(id) AS keep_id, COUNT(*) AS total_count
    FROM posts
    GROUP BY TRIM(title)
    HAVING COUNT(*) > 1
) k
    ON k.normalized_title = TRIM(p.title)
WHERE p.id <> k.keep_id;

UPDATE posts keep_post
JOIN tmp_duplicate_posts d
    ON d.keep_id = keep_post.id
JOIN posts old_post
    ON old_post.id = d.old_id
SET keep_post.view_count = GREATEST(COALESCE(keep_post.view_count, 0), COALESCE(old_post.view_count, 0)),
    keep_post.like_count = GREATEST(COALESCE(keep_post.like_count, 0), COALESCE(old_post.like_count, 0)),
    keep_post.comment_count = GREATEST(COALESCE(keep_post.comment_count, 0), COALESCE(old_post.comment_count, 0)),
    keep_post.is_pinned = (keep_post.is_pinned OR old_post.is_pinned),
    keep_post.is_featured = (keep_post.is_featured OR old_post.is_featured),
    keep_post.game_id = COALESCE(keep_post.game_id, old_post.game_id),
    keep_post.category = COALESCE(keep_post.category, old_post.category),
    keep_post.created_at = LEAST(keep_post.created_at, old_post.created_at),
    keep_post.updated_at = GREATEST(keep_post.updated_at, old_post.updated_at),
    keep_post.last_comment_at = CASE
        WHEN keep_post.last_comment_at IS NULL THEN old_post.last_comment_at
        WHEN old_post.last_comment_at IS NULL THEN keep_post.last_comment_at
        ELSE GREATEST(keep_post.last_comment_at, old_post.last_comment_at)
    END;

UPDATE comments c
JOIN tmp_duplicate_posts d
    ON d.old_id = c.post_id
SET c.post_id = d.keep_id;

UPDATE user_behavior_logs l
JOIN tmp_duplicate_posts d
    ON d.old_id = l.reference_id
SET l.reference_id = d.keep_id
WHERE l.behavior_type IN ('VIEW_FORUM_POST', 'CREATE_FORUM_POST', 'COMMENT_FORUM_POST');

DELETE p
FROM posts p
JOIN tmp_duplicate_posts d
    ON d.old_id = p.id;

-- 重新计算帖子评论数与最后评论时间，避免清理后统计失真
UPDATE posts p
LEFT JOIN (
    SELECT
        post_id,
        COUNT(*) AS real_comment_count,
        MAX(created_at) AS real_last_comment_at
    FROM comments
    WHERE status = 'PUBLISHED'
    GROUP BY post_id
) c
    ON c.post_id = p.id
SET p.comment_count = COALESCE(c.real_comment_count, 0),
    p.last_comment_at = c.real_last_comment_at;

COMMIT;

DROP TEMPORARY TABLE IF EXISTS tmp_duplicate_posts;
DROP TEMPORARY TABLE IF EXISTS tmp_duplicate_games;
DROP TEMPORARY TABLE IF EXISTS tmp_duplicate_categories;

-- ---------------------------------------------------------
-- 4. 为分类名 / 游戏名补充唯一约束，避免后续样本再次插入重名数据
-- ---------------------------------------------------------
SET @has_uk_categories_name = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'categories'
      AND index_name = 'uk_categories_name'
);

SET @sql_categories = IF(
    @has_uk_categories_name = 0,
    'ALTER TABLE categories ADD UNIQUE KEY uk_categories_name (name)',
    'SELECT 1'
);
PREPARE stmt_categories FROM @sql_categories;
EXECUTE stmt_categories;
DEALLOCATE PREPARE stmt_categories;

SET @has_uk_games_name = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'games'
      AND index_name = 'uk_games_name'
);

SET @sql_games = IF(
    @has_uk_games_name = 0,
    'ALTER TABLE games ADD UNIQUE KEY uk_games_name (name)',
    'SELECT 1'
);
PREPARE stmt_games FROM @sql_games;
EXECUTE stmt_games;
DEALLOCATE PREPARE stmt_games;
