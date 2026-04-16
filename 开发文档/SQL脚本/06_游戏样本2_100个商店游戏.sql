USE bishe;

-- =========================================================
-- 游戏样本2：新增 100 个商店游戏
-- 说明：
-- 1. 依赖 05_分类扩展与现有游戏类型补充.sql 中的扩展分类
-- 2. 通过 10 个世界观 × 10 个玩法类型 组合生成 100 个不重复游戏
-- 3. 每个游戏同时写入主分类与多分类关联，适合首页多选筛选演示
-- 4. 为避免 categories 表已有重名分类时导致一次执行插入重复游戏，
--    本脚本会先按分类名提取唯一分类映射，再执行 games / game_categories 插入
-- =========================================================

DROP TEMPORARY TABLE IF EXISTS tmp_sample2_styles;
CREATE TEMPORARY TABLE tmp_sample2_styles (
    id INT PRIMARY KEY,
    style_name VARCHAR(40) NOT NULL,
    primary_category_name VARCHAR(50) NOT NULL,
    secondary_category_name VARCHAR(50) NOT NULL,
    tertiary_category_name VARCHAR(50) NULL,
    base_tags VARCHAR(160) NOT NULL,
    description_prefix VARCHAR(120) NOT NULL,
    play_goal VARCHAR(120) NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    requirements VARCHAR(255) NOT NULL
) ENGINE=Memory;

INSERT INTO tmp_sample2_styles VALUES
(1, '开放世界动作', '动作游戏', '开放世界', '剧情向', '动作,开放世界,探索,单人,沉浸', '强调高速战斗与高自由探索的动作冒险', '区域推进、战斗成长和主线支线并行探索', 229.00, 'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&h=450&fit=crop', '推荐配置：Win10 / i5 / 16GB / RTX 2060 / 60GB'),
(2, '剧情角色扮演', '角色扮演', '剧情向', '开放世界', 'RPG,剧情,成长,世界观,任务驱动', '强调角色成长、剧情推进和队伍构筑的角色扮演', '剧情选择、流派成长与世界事件推进', 239.00, 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800&h=450&fit=crop', '推荐配置：Win10 / i5 / 16GB / GTX 1660 / 55GB'),
(3, '战术射击对抗', '射击游戏', '多人联机', '动作游戏', '射击,竞技,战术,多人联机,团队协作', '强调走位、配合与战术压制的多人射击', '据点推进、道具控制与团队配合取胜', 168.00, 'https://images.unsplash.com/photo-1511882150382-421056c89033?w=800&h=450&fit=crop', '推荐配置：Win10 / i5 / 16GB / RTX 2060 / 70GB'),
(4, '生存建造远征', '生存建造', '沙盒创造', '多人联机', '生存建造,采集,制造,基地经营,合作', '强调采集、建造和长期资源循环的生存冒险', '采集资源、建设基地与对抗环境危机', 136.00, 'https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=800&h=450&fit=crop', '推荐配置：Win10 / i5 / 16GB / GTX 1660 / 45GB'),
(5, '模拟经营工坊', '模拟经营', '休闲游戏', '沙盒创造', '模拟经营,养成,资源调度,经营,轻松', '强调经营节奏、生产链和长期养成反馈的模拟经营', '规划产线、扩张设施与优化收益效率', 96.00, 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&h=450&fit=crop', '推荐配置：Win10 / i3 / 8GB / GTX 1050 / 18GB'),
(6, '回合策略战棋', '策略游戏', '回合策略', '卡牌构筑', '回合策略,战棋,兵种搭配,资源管理,博弈', '强调部署顺序、数值取舍与地图博弈的回合策略', '部队编成、地形利用和回合压制', 119.00, 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&h=450&fit=crop', '推荐配置：Win10 / i3 / 8GB / 核显可运行 / 12GB'),
(7, '竞速驾驶巡回', '竞速驾驶', '体育竞技', '多人联机', '竞速驾驶,漂移,车辆调校,多人竞速,赛事', '强调操控手感、路线选择与车辆调校的竞速体验', '赛道冲刺、漂移节奏和车辆成长调校', 149.00, 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=450&fit=crop', '推荐配置：Win10 / i5 / 16GB / RTX 2060 / 50GB'),
(8, '恐怖解谜调查', '恐怖惊悚', '解谜探索', '剧情向', '恐怖,解谜,氛围,叙事,探索', '强调氛围压迫、线索拼接和调查推进的恐怖解谜', '收集线索、破解机关与还原事件真相', 99.00, 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=800&h=450&fit=crop', '推荐配置：Win10 / i5 / 8GB / GTX 1060 / 22GB'),
(9, '卡牌构筑试炼', '卡牌构筑', 'Roguelike肉鸽', '策略游戏', '卡牌构筑,肉鸽,流派,随机事件,高重玩', '强调构筑取舍、随机路线和局内成长的卡牌策略', '卡组优化、事件抉择与阶段 Boss 挑战', 88.00, 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800&h=450&fit=crop', '推荐配置：Win10 / i3 / 8GB / 核显可运行 / 10GB'),
(10, '音游派对挑战', '音乐节奏', '派对欢乐', '休闲游戏', '音乐节奏,派对,多人同乐,轻竞技,节拍', '强调节拍判定、舞台反馈与派对气氛的音乐挑战', '节奏连击、派对对抗与舞台成就收集', 76.00, 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=800&h=450&fit=crop', '推荐配置：Win10 / i3 / 8GB / 核显可运行 / 8GB');

DROP TEMPORARY TABLE IF EXISTS tmp_sample2_worlds;
CREATE TEMPORARY TABLE tmp_sample2_worlds (
    id INT PRIMARY KEY,
    world_name VARCHAR(40) NOT NULL,
    world_desc VARCHAR(160) NOT NULL,
    world_tags VARCHAR(160) NOT NULL
) ENGINE=Memory;

INSERT INTO tmp_sample2_worlds VALUES
(1, '赛博都市', '霓虹高楼、地下网络与企业势力并存的未来都市', '赛博科幻,都市,黑客,霓虹'),
(2, '中世纪王国', '骑士、领主与古老遗迹交错的战乱王国', '奇幻,王国,骑士,古堡'),
(3, '星际边境', '空间站、矿业殖民地与未知星云环绕的深空前线', '太空,科幻,星际,边境'),
(4, '末日废土', '资源稀缺、风暴肆虐且秩序崩解的残存世界', '末日,废土,生存,资源争夺'),
(5, '蒸汽群岛', '空艇与机械工坊遍布、技术派系互相竞争的群岛', '蒸汽朋克,机械,空艇,群岛'),
(6, '神话大陆', '神祇传说、遗迹祭坛与古代种族并存的幻想大陆', '神话,奇幻,遗迹,传说'),
(7, '未来竞技城', '被赛事与直播文化统治的高科技竞技都市', '竞技,未来,赛事,都市'),
(8, '海洋群岛', '港口贸易、海盗势力和深海秘密交织的航海区域', '航海,群岛,贸易,深海'),
(9, '东方幻想乡', '符咒、妖怪和秘境仪式共同构成的东方幻想舞台', '东方幻想,秘境,妖怪,和风'),
(10, '机械星域', '人工智能、巨型工厂与轨道设施密布的工业星域', '机械,工业,科幻,星域');

DROP TEMPORARY TABLE IF EXISTS tmp_sample2_themes;
CREATE TEMPORARY TABLE tmp_sample2_themes (
    id INT PRIMARY KEY,
    theme_name VARCHAR(40) NOT NULL,
    story_hook VARCHAR(160) NOT NULL
) ENGINE=Memory;

INSERT INTO tmp_sample2_themes VALUES
(1, '远征计划', '一项高风险远征计划意外引发了地区局势连锁失控'),
(2, '边境传说', '一段被遗忘的边境传说正在逐步映照到现实冲突中'),
(3, '风暴协议', '围绕神秘协议展开的博弈改变了各方阵营力量平衡'),
(4, '失落档案', '被封存多年的失落档案揭开了世界深层真相'),
(5, '黎明防线', '在全面崩坏前建立最后防线成为所有行动的核心目标'),
(6, '暗影回响', '接连出现的暗影回响事件迫使玩家不断深入调查'),
(7, '天穹旅团', '一支被寄予厚望的旅团必须重新集结并完成关键使命'),
(8, '纪元裂隙', '突如其来的纪元裂隙让多个时代和势力发生碰撞'),
(9, '终局信标', '唯一仍在运作的终局信标成为各方争夺焦点'),
(10, '星火联盟', '草根组织星火联盟正在悄然改变整个区域格局');

DROP TEMPORARY TABLE IF EXISTS tmp_sample2_games;
CREATE TEMPORARY TABLE tmp_sample2_games AS
SELECT
    ((w.id - 1) * 10 + s.id) AS seq_no,
    CONCAT(w.world_name, '：', t.theme_name) AS game_name,
    s.primary_category_name,
    s.secondary_category_name,
    s.tertiary_category_name,
    CONCAT(
        '《', w.world_name, '：', t.theme_name, '》是一款', s.description_prefix,
        '作品。故事围绕', t.story_hook, '展开，玩家将在', w.world_desc,
        '中完成', s.play_goal, '，并体验 ', s.base_tags, ' 所构成的复合型玩法循环。'
    ) AS description_text,
    CONCAT(w.world_name, ' Studio') AS developer_name,
    CONCAT(s.style_name, ' Publishing') AS publisher_name,
    DATE_ADD('2021-01-01', INTERVAL (((w.id - 1) * 10 + s.id) * 13) DAY) AS release_date_value,
    ROUND(s.base_price + w.id * 8 + s.id * 5, 2) AS price_value,
    CASE
        WHEN MOD(((w.id - 1) * 10 + s.id), 4) = 0 THEN ROUND((s.base_price + w.id * 8 + s.id * 5) * 0.78, 2)
        WHEN MOD(((w.id - 1) * 10 + s.id), 5) = 0 THEN ROUND((s.base_price + w.id * 8 + s.id * 5) * 0.85, 2)
        ELSE NULL
    END AS discount_price_value,
    s.image_url,
    s.requirements,
    CONCAT(s.base_tags, ',', w.world_tags, ',', t.theme_name) AS tags_text,
    ROUND(3.85 + (MOD(w.id * 3 + s.id * 2, 10) * 0.09), 2) AS rating_value,
    900 + w.id * 150 + s.id * 120 AS rating_count_value,
    3200 + w.id * 540 + s.id * 360 AS download_count_value,
    CASE WHEN MOD(((w.id - 1) * 10 + s.id), 18) = 0 THEN TRUE ELSE FALSE END AS featured_value
FROM tmp_sample2_worlds w
CROSS JOIN tmp_sample2_styles s
JOIN tmp_sample2_themes t
    ON t.id = MOD(w.id + s.id - 2, 10) + 1;

DROP TEMPORARY TABLE IF EXISTS tmp_sample2_category_map;
CREATE TEMPORARY TABLE tmp_sample2_category_map AS
SELECT
    name,
    MIN(id) AS category_id
FROM categories
GROUP BY name;

INSERT INTO games (
    name,
    description,
    category_id,
    developer,
    publisher,
    release_date,
    price,
    discount_price,
    image_url,
    system_requirements,
    tags,
    rating,
    rating_count,
    download_count,
    is_featured,
    status
)
SELECT
    seed.game_name,
    seed.description_text,
    primary_category.category_id,
    seed.developer_name,
    seed.publisher_name,
    seed.release_date_value,
    seed.price_value,
    seed.discount_price_value,
    seed.image_url,
    seed.requirements,
    seed.tags_text,
    seed.rating_value,
    seed.rating_count_value,
    seed.download_count_value,
    seed.featured_value,
    'ACTIVE'
FROM tmp_sample2_games seed
JOIN tmp_sample2_category_map primary_category
    ON primary_category.name = seed.primary_category_name
LEFT JOIN games existing
    ON TRIM(existing.name) = TRIM(seed.game_name)
WHERE existing.id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT game_ref.id, category_ref.category_id
FROM tmp_sample2_games mapping
JOIN games game_ref
    ON TRIM(game_ref.name) = TRIM(mapping.game_name)
JOIN tmp_sample2_category_map category_ref
    ON category_ref.name = mapping.primary_category_name
LEFT JOIN game_categories gc
    ON gc.game_id = game_ref.id AND gc.category_id = category_ref.category_id
WHERE gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT game_ref.id, category_ref.category_id
FROM tmp_sample2_games mapping
JOIN games game_ref
    ON TRIM(game_ref.name) = TRIM(mapping.game_name)
JOIN tmp_sample2_category_map category_ref
    ON category_ref.name = mapping.secondary_category_name
LEFT JOIN game_categories gc
    ON gc.game_id = game_ref.id AND gc.category_id = category_ref.category_id
WHERE gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT game_ref.id, category_ref.category_id
FROM tmp_sample2_games mapping
JOIN games game_ref
    ON TRIM(game_ref.name) = TRIM(mapping.game_name)
JOIN tmp_sample2_category_map category_ref
    ON category_ref.name = mapping.tertiary_category_name
LEFT JOIN game_categories gc
    ON gc.game_id = game_ref.id AND gc.category_id = category_ref.category_id
WHERE mapping.tertiary_category_name IS NOT NULL
  AND gc.game_id IS NULL;

DROP TEMPORARY TABLE IF EXISTS tmp_sample2_category_map;
DROP TEMPORARY TABLE IF EXISTS tmp_sample2_games;
DROP TEMPORARY TABLE IF EXISTS tmp_sample2_themes;
DROP TEMPORARY TABLE IF EXISTS tmp_sample2_worlds;
DROP TEMPORARY TABLE IF EXISTS tmp_sample2_styles;
