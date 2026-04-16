USE bishe;

-- =========================================================
-- 分类扩展与现有游戏类型补充
-- 说明：
-- 1. 为首页多选筛选补充更完整的游戏类型分类
-- 2. 为现有示例游戏补充多分类关联和更丰富的标签
-- 3. 可在 02_基础演示数据.sql（游戏样本1）之后执行
-- =========================================================

INSERT INTO categories (name, description, sort_order, status)
SELECT '开放世界', '强调高自由度地图探索与区域推进', 7, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '开放世界');

INSERT INTO categories (name, description, sort_order, status)
SELECT '剧情向', '强调叙事表现、角色塑造与沉浸式流程', 8, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '剧情向');

INSERT INTO categories (name, description, sort_order, status)
SELECT '生存建造', '围绕采集、制造、基地建设与资源循环', 9, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '生存建造');

INSERT INTO categories (name, description, sort_order, status)
SELECT '模拟经营', '以模拟、经营、养成和资源调配为核心', 10, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '模拟经营');

INSERT INTO categories (name, description, sort_order, status)
SELECT '回合策略', '围绕回合制部署、博弈和战术规划', 11, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '回合策略');

INSERT INTO categories (name, description, sort_order, status)
SELECT '竞速驾驶', '以竞速、漂移、车辆调校和赛道对抗为核心', 12, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '竞速驾驶');

INSERT INTO categories (name, description, sort_order, status)
SELECT '体育竞技', '以球类、极限运动或竞技赛事为主题', 13, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '体育竞技');

INSERT INTO categories (name, description, sort_order, status)
SELECT '恐怖惊悚', '围绕恐怖氛围、生存压力和惊悚叙事展开', 14, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '恐怖惊悚');

INSERT INTO categories (name, description, sort_order, status)
SELECT '解谜探索', '强调机关破解、线索拼接与探索式推进', 15, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '解谜探索');

INSERT INTO categories (name, description, sort_order, status)
SELECT 'Roguelike肉鸽', '强调随机生成、循环成长与高重玩价值', 16, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Roguelike肉鸽');

INSERT INTO categories (name, description, sort_order, status)
SELECT '卡牌构筑', '强调卡组构筑、策略组合和数值取舍', 17, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '卡牌构筑');

INSERT INTO categories (name, description, sort_order, status)
SELECT '沙盒创造', '强调自由编辑、世界搭建与创造性玩法', 18, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '沙盒创造');

INSERT INTO categories (name, description, sort_order, status)
SELECT '多人联机', '强调在线组队、竞技或合作互动', 19, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '多人联机');

INSERT INTO categories (name, description, sort_order, status)
SELECT '音乐节奏', '以音符判定、节拍挑战和音乐表现为核心', 20, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '音乐节奏');

INSERT INTO categories (name, description, sort_order, status)
SELECT '派对欢乐', '强调多人同乐、轻度竞技和聚会氛围', 21, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '派对欢乐');

INSERT INTO categories (name, description, sort_order, status)
SELECT '平台跳跃', '以移动、跳跃、关卡挑战和操作精度为核心', 22, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '平台跳跃');

INSERT INTO categories (name, description, sort_order, status)
SELECT '格斗对战', '强调连招、博弈和近身对抗', 23, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '格斗对战');

INSERT INTO categories (name, description, sort_order, status)
SELECT '塔防策略', '以路线规划、塔组组合和资源节奏为核心', 24, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '塔防策略');

INSERT INTO categories (name, description, sort_order, status)
SELECT 'MMO大型多人', '强调长期养成、多人协作和公会社交', 25, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'MMO大型多人');

INSERT INTO categories (name, description, sort_order, status)
SELECT '视觉小说', '以文本叙事、分支选择和角色互动为核心', 26, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '视觉小说');

INSERT INTO categories (name, description, sort_order, status)
SELECT '魂like', '强调高压战斗、死亡学习和关卡压迫感', 27, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '魂like');

-- 先补齐主分类映射，避免历史数据只有 games.category_id 没有 game_categories
INSERT INTO game_categories (game_id, category_id)
SELECT g.id, g.category_id
FROM games g
LEFT JOIN game_categories gc
    ON gc.game_id = g.id AND gc.category_id = g.category_id
WHERE g.category_id IS NOT NULL
  AND gc.game_id IS NULL;

SET @cat_open_world = (SELECT id FROM categories WHERE name = '开放世界' LIMIT 1);
SET @cat_story = (SELECT id FROM categories WHERE name = '剧情向' LIMIT 1);
SET @cat_survival = (SELECT id FROM categories WHERE name = '生存建造' LIMIT 1);
SET @cat_sim = (SELECT id FROM categories WHERE name = '模拟经营' LIMIT 1);
SET @cat_turn = (SELECT id FROM categories WHERE name = '回合策略' LIMIT 1);
SET @cat_race = (SELECT id FROM categories WHERE name = '竞速驾驶' LIMIT 1);
SET @cat_sports = (SELECT id FROM categories WHERE name = '体育竞技' LIMIT 1);
SET @cat_horror = (SELECT id FROM categories WHERE name = '恐怖惊悚' LIMIT 1);
SET @cat_puzzle = (SELECT id FROM categories WHERE name = '解谜探索' LIMIT 1);
SET @cat_rogue = (SELECT id FROM categories WHERE name = 'Roguelike肉鸽' LIMIT 1);
SET @cat_card = (SELECT id FROM categories WHERE name = '卡牌构筑' LIMIT 1);
SET @cat_sandbox = (SELECT id FROM categories WHERE name = '沙盒创造' LIMIT 1);
SET @cat_multi = (SELECT id FROM categories WHERE name = '多人联机' LIMIT 1);
SET @cat_music = (SELECT id FROM categories WHERE name = '音乐节奏' LIMIT 1);
SET @cat_party = (SELECT id FROM categories WHERE name = '派对欢乐' LIMIT 1);
SET @cat_platform = (SELECT id FROM categories WHERE name = '平台跳跃' LIMIT 1);
SET @cat_fight = (SELECT id FROM categories WHERE name = '格斗对战' LIMIT 1);
SET @cat_td = (SELECT id FROM categories WHERE name = '塔防策略' LIMIT 1);
SET @cat_mmo = (SELECT id FROM categories WHERE name = 'MMO大型多人' LIMIT 1);
SET @cat_vn = (SELECT id FROM categories WHERE name = '视觉小说' LIMIT 1);
SET @cat_souls = (SELECT id FROM categories WHERE name = '魂like' LIMIT 1);

-- 现有样本游戏多分类补充
INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_open_world FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_open_world
WHERE g.name = '赛博朋克2077' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_story FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_story
WHERE g.name = '赛博朋克2077' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_open_world FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_open_world
WHERE g.name = '巫师3：狂猎' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_story FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_story
WHERE g.name = '巫师3：狂猎' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_open_world FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_open_world
WHERE g.name = '艾尔登法环' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_souls FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_souls
WHERE g.name = '艾尔登法环' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_multi FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_multi
WHERE g.name = '使命召唤：现代战争III' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_open_world FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_open_world
WHERE g.name = '荒野大镖客：救赎2' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_story FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_story
WHERE g.name = '荒野大镖客：救赎2' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_turn FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_turn
WHERE g.name = '文明VI' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_multi FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_multi
WHERE g.name = 'Apex 英雄' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_multi FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_multi
WHERE g.name = '无畏契约' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_souls FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_souls
WHERE g.name = '只狼：影逝二度' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_story FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_story
WHERE g.name = '对马岛之魂' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_open_world FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_open_world
WHERE g.name = '对马岛之魂' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_sim FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_sim
WHERE g.name = '星露谷物语' AND gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, @cat_sandbox FROM games g
LEFT JOIN game_categories gc ON gc.game_id = g.id AND gc.category_id = @cat_sandbox
WHERE g.name = '星露谷物语' AND gc.game_id IS NULL;

-- 可见标签补充，便于首页卡片和推荐系统展示
UPDATE games
SET tags = '开放世界,剧情向,RPG,赛博科幻,第一人称'
WHERE name = '赛博朋克2077';

UPDATE games
SET tags = '开放世界,剧情向,RPG,奇幻,任务驱动'
WHERE name = '巫师3：狂猎';

UPDATE games
SET tags = '开放世界,魂like,动作RPG,高难度,奇幻'
WHERE name = '艾尔登法环';

UPDATE games
SET tags = '多人联机,战术射击,动作,军事,竞技'
WHERE name = '使命召唤：现代战争III';

UPDATE games
SET tags = '开放世界,剧情向,冒险,西部,沉浸式'
WHERE name = '荒野大镖客：救赎2';

UPDATE games
SET tags = '回合策略,经营,历史,文明发展,战术规划'
WHERE name = '文明VI';

UPDATE games
SET tags = '多人联机,大逃杀,英雄射击,免费,竞技'
WHERE name = 'Apex 英雄';

UPDATE games
SET tags = '多人联机,战术射击,免费,竞技,团队协作'
WHERE name = '无畏契约';

UPDATE games
SET tags = '魂like,动作冒险,高难度,忍者,单人挑战'
WHERE name = '只狼：影逝二度';

UPDATE games
SET tags = '开放世界,剧情向,动作冒险,武士,探索'
WHERE name = '对马岛之魂';

UPDATE games
SET tags = '模拟经营,农场,休闲,像素风,沙盒创造'
WHERE name = '星露谷物语';
