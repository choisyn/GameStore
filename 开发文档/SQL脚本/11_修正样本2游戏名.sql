USE bishe;

-- =========================================================
-- 将样本2生成的“世界名：玩法风格·主题名”修正为“世界名：主题名”
-- 说明：
-- 1. 只处理 06_游戏样本2_100个商店游戏.sql 生成的样本数据
-- 2. 会同步修正 games / order_items / posts 中引用到的旧名称
-- 3. 脚本可重复执行；修正完成后再次执行不会重复改名
-- =========================================================

DROP TEMPORARY TABLE IF EXISTS tmp_sample2_name_fix;
CREATE TEMPORARY TABLE tmp_sample2_name_fix AS
SELECT
    g.id AS game_id,
    TRIM(g.name) AS old_name,
    CONCAT(
        TRIM(SUBSTRING_INDEX(g.name, '：', 1)),
        '：',
        TRIM(SUBSTRING_INDEX(g.name, '·', -1))
    ) AS new_name
FROM games g
WHERE g.name LIKE '%：%·%'
  AND COALESCE(g.developer, '') LIKE '% Studio'
  AND COALESCE(g.publisher, '') LIKE '% Publishing';

-- 如果这里查出结果，说明新名称仍有重名风险，应先停止执行并检查数据
SELECT new_name, COUNT(*) AS total
FROM tmp_sample2_name_fix
GROUP BY new_name
HAVING COUNT(*) > 1;

UPDATE games g
JOIN tmp_sample2_name_fix f
    ON f.game_id = g.id
SET
    g.name = f.new_name,
    g.description = CASE
        WHEN g.description IS NULL THEN NULL
        ELSE REPLACE(g.description, CONCAT('《', f.old_name, '》'), CONCAT('《', f.new_name, '》'))
    END;

UPDATE order_items oi
JOIN tmp_sample2_name_fix f
    ON f.game_id = oi.game_id
SET oi.game_name = f.new_name;

UPDATE posts p
JOIN tmp_sample2_name_fix f
    ON f.game_id = p.game_id
SET
    p.title = REPLACE(p.title, f.old_name, f.new_name),
    p.content = REPLACE(p.content, f.old_name, f.new_name);

SELECT COUNT(*) AS fixed_games
FROM tmp_sample2_name_fix;

DROP TEMPORARY TABLE IF EXISTS tmp_sample2_name_fix;
