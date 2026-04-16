USE bishe;

-- =========================================================
-- Steam 真实热门单机游戏样本：100 个
-- 说明：
-- 1. 本脚本使用 Steam 上真实存在且热门的单机/单人可玩作品名称
-- 2. 标题、开发商、发行商、发售日期按 Steam 商店公开信息整理
-- 3. 价格、评分、下载量用于商城演示展示，不等同于 Steam 实时动态数据
-- 4. 依赖 05_分类扩展与现有游戏类型补充.sql 中的扩展分类
-- 5. 名称去重插入，可重复执行，不会重复写入同名游戏
-- =========================================================

-- =========================================================
-- 旧库去重预处理
-- 说明：
-- 1. 先删除旧库里偏网游/联机竞技/占位脏数据，避免与单机样本混杂
-- 2. 再把旧库里需要保留的游戏名规范化，和本脚本/13号脚本保持一致
-- 3. 删除 games 时，game_categories / user_games / cart_items / order_items 会按外键级联处理
-- =========================================================

DROP TEMPORARY TABLE IF EXISTS tmp_legacy_games_to_delete;
CREATE TEMPORARY TABLE tmp_legacy_games_to_delete (
    game_name VARCHAR(100) PRIMARY KEY
) ENGINE=Memory;

INSERT INTO tmp_legacy_games_to_delete (game_name) VALUES
('123'),
('Apex英雄'),
('Among Us'),
('CS:GO反恐精英'),
('DOTA 2'),
('使命召唤：现代战争III'),
('堡垒之夜'),
('守望先锋2'),
('无畏契约'),
('糖豆人:终极淘汰赛'),
('英雄联盟'),
('最终幻想XIV'),
('绝地求生'),
('荒野大镖客2');

DELETE g
FROM games g
JOIN tmp_legacy_games_to_delete d
    ON TRIM(g.name) = TRIM(d.game_name);

SELECT ROW_COUNT() AS deleted_legacy_games;

DROP TEMPORARY TABLE IF EXISTS tmp_legacy_games_to_delete;

DROP TEMPORARY TABLE IF EXISTS tmp_legacy_name_fix;
CREATE TEMPORARY TABLE tmp_legacy_name_fix (
    old_name VARCHAR(100) PRIMARY KEY,
    new_name VARCHAR(100) NOT NULL
) ENGINE=Memory;

INSERT INTO tmp_legacy_name_fix (old_name, new_name) VALUES
('上古卷轴5:天际', '上古卷轴5：天际'),
('只狼:影逝二度', '只狼：影逝二度'),
('城市:天际线', '都市：天际线'),
('全面战争:三国', '全面战争：三国'),
('尼尔:机械纪元', '尼尔：机械纪元'),
('彩虹六号:围攻', '彩虹六号：围攻'),
('文明6', '文明VI'),
('极限竞速:地平线5', '极限竞速：地平线5');

-- 若改名后会撞到已存在的标准名称，先删掉旧别名，避免 update 产生重复
DELETE old_game
FROM games old_game
JOIN tmp_legacy_name_fix f
    ON TRIM(old_game.name) = TRIM(f.old_name)
JOIN games canonical_game
    ON TRIM(canonical_game.name) = TRIM(f.new_name);

UPDATE games g
JOIN tmp_legacy_name_fix f
    ON TRIM(g.name) = TRIM(f.old_name)
SET
    g.name = f.new_name,
    g.description = CASE
        WHEN g.description IS NULL THEN NULL
        ELSE REPLACE(g.description, f.old_name, f.new_name)
    END;

UPDATE order_items oi
JOIN tmp_legacy_name_fix f
    ON TRIM(oi.game_name) = TRIM(f.old_name)
SET oi.game_name = f.new_name;

UPDATE posts p
JOIN tmp_legacy_name_fix f
    ON TRIM(p.title) LIKE CONCAT('%', TRIM(f.old_name), '%')
    OR TRIM(p.content) LIKE CONCAT('%', TRIM(f.old_name), '%')
SET
    p.title = REPLACE(p.title, f.old_name, f.new_name),
    p.content = REPLACE(p.content, f.old_name, f.new_name);

SELECT COUNT(*) AS legacy_name_fix_rules
FROM tmp_legacy_name_fix;

DROP TEMPORARY TABLE IF EXISTS tmp_legacy_name_fix;

DROP TEMPORARY TABLE IF EXISTS tmp_real_steam_games;
CREATE TEMPORARY TABLE tmp_real_steam_games (
    id INT PRIMARY KEY,
    game_name VARCHAR(100) NOT NULL,
    primary_category_name VARCHAR(50) NOT NULL,
    secondary_category_name VARCHAR(50) NOT NULL,
    tertiary_category_name VARCHAR(50) NULL,
    developer_name VARCHAR(100) NOT NULL,
    publisher_name VARCHAR(100) NOT NULL,
    release_date_value DATE NOT NULL,
    price_value DECIMAL(10, 2) NOT NULL,
    tags_text VARCHAR(200) NOT NULL
) ENGINE=Memory;

INSERT INTO tmp_real_steam_games VALUES
(1, '赛博朋克2077', '角色扮演', '开放世界', '剧情向', 'CD PROJEKT RED', 'CD PROJEKT RED', '2020-12-09', 298.00, 'RPG,开放世界,剧情,赛博朋克,第一人称'),
(2, '荒野大镖客：救赎2', '动作游戏', '开放世界', '剧情向', 'Rockstar Games', 'Rockstar Games', '2019-12-05', 279.00, '开放世界,剧情,西部,动作,第三人称'),
(3, '对马岛之魂：导演剪辑版', '动作游戏', '开放世界', '剧情向', 'Sucker Punch Productions', 'PlayStation Publishing LLC', '2024-05-16', 398.00, '开放世界,武士,剧情,动作,日本'),
(4, '艾尔登法环', '魂like', '开放世界', '角色扮演', 'FromSoftware, Inc.', 'Bandai Namco Entertainment', '2022-02-24', 298.00, '魂like,开放世界,RPG,动作,高难度'),
(5, '黑神话：悟空', '动作游戏', '魂like', '剧情向', 'Game Science', 'Game Science', '2024-08-20', 268.00, '动作,神话,剧情,魂like,单人'),
(6, '霍格沃茨之遗', '角色扮演', '开放世界', '剧情向', 'Avalanche Software', 'Warner Bros. Games', '2023-02-10', 298.00, 'RPG,开放世界,魔法,剧情,单人'),
(7, '地平线：零之曙光 完整版', '动作游戏', '开放世界', '剧情向', 'Guerrilla', 'PlayStation Publishing LLC', '2020-08-07', 248.00, '开放世界,动作,剧情,科幻,探索'),
(8, '地平线：西之绝境 完整版', '动作游戏', '开放世界', '剧情向', 'Guerrilla', 'PlayStation Publishing LLC', '2024-03-21', 345.00, '开放世界,动作,剧情,科幻,冒险'),
(9, '战神', '动作游戏', '剧情向', '冒险游戏', 'Santa Monica Studio', 'PlayStation Publishing LLC', '2022-01-14', 249.00, '动作,剧情,神话,冒险,单人'),
(10, '战神：诸神黄昏', '动作游戏', '剧情向', '冒险游戏', 'Santa Monica Studio', 'PlayStation Publishing LLC', '2024-09-19', 398.00, '动作,剧情,神话,冒险,单人'),

(11, '漫威蜘蛛侠：重制版', '动作游戏', '开放世界', '剧情向', 'Insomniac Games', 'PlayStation Publishing LLC', '2022-08-12', 249.00, '动作,开放世界,超级英雄,剧情,单人'),
(12, '漫威蜘蛛侠：迈尔斯·莫拉莱斯', '动作游戏', '开放世界', '剧情向', 'Insomniac Games', 'PlayStation Publishing LLC', '2022-11-18', 279.00, '动作,开放世界,超级英雄,剧情,单人'),
(13, '漫威蜘蛛侠2', '动作游戏', '开放世界', '剧情向', 'Insomniac Games', 'PlayStation Publishing LLC', '2025-01-30', 398.00, '动作,开放世界,超级英雄,剧情,单人'),
(14, '死亡搁浅：导演剪辑版', '冒险游戏', '剧情向', '开放世界', 'KOJIMA PRODUCTIONS', '505 Games', '2022-03-30', 198.00, '剧情,开放世界,冒险,科幻,探索'),
(15, '往日不再', '动作游戏', '开放世界', '剧情向', 'Bend Studio', 'PlayStation Publishing LLC', '2021-05-18', 198.00, '动作,开放世界,剧情,末日,单人'),
(16, '最后生还者：第一部', '冒险游戏', '剧情向', '动作游戏', 'Naughty Dog LLC', 'PlayStation Publishing LLC', '2023-03-28', 398.00, '剧情,冒险,动作,末日,单人'),
(17, '神秘海域：盗贼传奇合辑', '冒险游戏', '剧情向', '动作游戏', 'Naughty Dog LLC', 'PlayStation Publishing LLC', '2022-10-19', 249.00, '剧情,冒险,动作,寻宝,单人'),
(18, '星球大战 绝地：陨落的武士团', '动作游戏', '剧情向', '冒险游戏', 'Respawn Entertainment', 'Electronic Arts', '2019-11-15', 198.00, '动作,剧情,星战,冒险,单人'),
(19, '星球大战 绝地：幸存者', '动作游戏', '剧情向', '冒险游戏', 'Respawn Entertainment', 'Electronic Arts', '2023-04-28', 298.00, '动作,剧情,星战,冒险,单人'),
(20, '只狼：影逝二度 年度版', '动作游戏', '魂like', '剧情向', 'FromSoftware, Inc.', 'Activision', '2019-03-22', 268.00, '动作,魂like,忍者,剧情,高难度'),

(21, '博德之门3', '角色扮演', '剧情向', '回合策略', 'Larian Studios', 'Larian Studios', '2023-08-03', 298.00, 'RPG,剧情,回合制,CRPG,单人'),
(22, '巫师3：狂猎', '角色扮演', '开放世界', '剧情向', 'CD PROJEKT RED', 'CD PROJEKT RED', '2015-05-18', 149.00, 'RPG,开放世界,剧情,奇幻,单人'),
(23, '神界：原罪2 决定版', '角色扮演', '回合策略', '剧情向', 'Larian Studios', 'Larian Studios', '2017-09-14', 133.00, 'RPG,回合制,剧情,CRPG,单人'),
(24, '极乐迪斯科：最终剪辑版', '角色扮演', '剧情向', '冒险游戏', 'ZA/UM', 'ZA/UM', '2019-10-15', 116.00, 'RPG,剧情,文字,侦探,单人'),
(25, '战锤40K：行商浪人', '角色扮演', '回合策略', '剧情向', 'Owlcat Games', 'Owlcat Games', '2023-12-07', 198.00, 'RPG,回合制,剧情,战锤,单人'),
(26, '开拓者：正义之怒 增强版', '角色扮演', '剧情向', '回合策略', 'Owlcat Games', 'META Publishing', '2021-09-02', 188.00, 'RPG,剧情,回合制,奇幻,单人'),
(27, '女神异闻录5：皇家版', '角色扮演', '剧情向', '回合策略', 'ATLUS', 'SEGA', '2022-10-21', 329.00, 'JRPG,剧情,回合制,校园,单人'),
(28, '女神异闻录3 Reload', '角色扮演', '剧情向', '回合策略', 'ATLUS', 'SEGA', '2024-02-01', 349.00, 'JRPG,剧情,回合制,校园,单人'),
(29, '暗喻幻想：ReFantazio', '角色扮演', '剧情向', '回合策略', 'ATLUS', 'SEGA', '2024-10-11', 398.00, 'JRPG,剧情,回合制,奇幻,单人'),
(30, '人中之龙8', '角色扮演', '剧情向', '冒险游戏', 'Ryu Ga Gotoku Studio', 'SEGA', '2024-01-26', 349.00, 'RPG,剧情,都市,冒险,单人'),

(31, '人中之龙7：光与暗的去向 国际版', '角色扮演', '剧情向', '冒险游戏', 'Ryu Ga Gotoku Studio', 'SEGA', '2020-11-10', 189.00, 'RPG,剧情,都市,冒险,单人'),
(32, '尼尔：机械纪元', '角色扮演', '动作游戏', '剧情向', 'PlatinumGames Inc.', 'Square Enix', '2017-03-17', 274.00, '动作RPG,剧情,科幻,单人,日式'),
(33, '尼尔：人工生命 ver.1.22474487139...', '角色扮演', '动作游戏', '剧情向', 'Toylogic Inc.', 'Square Enix', '2021-04-23', 359.00, '动作RPG,剧情,奇幻,单人,日式'),
(34, '最终幻想7：重制版 INTERGRADE', '角色扮演', '剧情向', '动作游戏', 'Square Enix', 'Square Enix', '2022-06-17', 446.00, 'JRPG,剧情,动作,单人,经典重制'),
(35, '最终幻想16', '角色扮演', '动作游戏', '剧情向', 'Square Enix', 'Square Enix', '2024-09-17', 398.00, 'JRPG,动作,剧情,奇幻,单人'),
(36, '最终幻想15 Windows版', '角色扮演', '开放世界', '动作游戏', 'Square Enix', 'Square Enix', '2018-03-07', 230.00, 'JRPG,开放世界,动作,剧情,单人'),
(37, '勇者斗恶龙11S：寻觅逝去的时光 决定版', '角色扮演', '剧情向', '回合策略', 'Square Enix', 'Square Enix', '2020-12-04', 349.00, 'JRPG,回合制,剧情,奇幻,单人'),
(38, '歧路旅人II', '角色扮演', '剧情向', '回合策略', 'ACQUIRE Corp.', 'Square Enix', '2023-02-24', 379.00, 'JRPG,回合制,剧情,像素,单人'),
(39, '破晓传说', '角色扮演', '动作游戏', '剧情向', 'Bandai Namco Studios Inc.', 'Bandai Namco Entertainment', '2021-09-10', 328.00, 'JRPG,动作,剧情,奇幻,单人'),
(40, '龙之信条2', '角色扮演', '开放世界', '动作游戏', 'CAPCOM Co., Ltd.', 'CAPCOM Co., Ltd.', '2024-03-22', 348.00, '动作RPG,开放世界,奇幻,单人,冒险'),

(41, '文明VI', '策略游戏', '回合策略', NULL, 'Firaxis Games', '2K', '2016-10-21', 199.00, '策略,回合制,文明发展,历史,单人'),
(42, '文明VII', '策略游戏', '回合策略', NULL, 'Firaxis Games', '2K', '2025-02-10', 398.00, '策略,回合制,文明发展,历史,单人'),
(43, '奇迹时代4', '策略游戏', '回合策略', '角色扮演', 'Triumph Studios', 'Paradox Interactive', '2023-05-02', 198.00, '策略,回合制,奇幻,4X,单人'),
(44, '群星', '策略游戏', '沙盒创造', '模拟经营', 'Paradox Development Studio', 'Paradox Interactive', '2016-05-09', 188.00, '策略,太空,经营,4X,单人'),
(45, '十字军之王3', '策略游戏', '模拟经营', NULL, 'Paradox Development Studio', 'Paradox Interactive', '2020-09-01', 198.00, '策略,中世纪,经营,历史,单人'),
(46, '钢铁雄心4', '策略游戏', '模拟经营', NULL, 'Paradox Development Studio', 'Paradox Interactive', '2016-06-06', 188.00, '策略,战争,历史,经营,单人'),
(47, '欧陆风云4', '策略游戏', '模拟经营', NULL, 'Paradox Development Studio', 'Paradox Interactive', '2013-08-13', 148.00, '策略,历史,经营,全球地图,单人'),
(48, '全面战争：战锤3', '策略游戏', '回合策略', NULL, 'Creative Assembly', 'SEGA', '2022-02-16', 268.00, '策略,回合制,战争,奇幻,单人'),
(49, '全面战争：三国', '策略游戏', '回合策略', NULL, 'Creative Assembly', 'SEGA', '2019-05-23', 268.00, '策略,回合制,三国,战争,单人'),
(50, '幽浮2', '策略游戏', '回合策略', '射击游戏', 'Firaxis Games', '2K', '2016-02-05', 190.00, '策略,回合制,战术,科幻,单人'),

(51, '环世界', '策略游戏', '模拟经营', '沙盒创造', 'Ludeon Studios', 'Ludeon Studios', '2018-10-17', 128.00, '模拟经营,沙盒,殖民地,策略,单人'),
(52, '异星工厂', '模拟经营', '沙盒创造', '策略游戏', 'Wube Software LTD.', 'Wube Software LTD.', '2020-08-14', 130.00, '模拟经营,工厂,自动化,沙盒,单人'),
(53, '戴森球计划', '模拟经营', '沙盒创造', '策略游戏', 'Youthcat Studio', 'Gamera Games', '2021-01-21', 108.00, '模拟经营,工厂,太空,自动化,单人'),
(54, '风暴之城', '模拟经营', '策略游戏', '沙盒创造', 'Eremite Games', 'Hooded Horse', '2023-12-08', 128.00, '模拟经营,策略,建造,奇幻,单人'),
(55, '冰汽时代', '模拟经营', '策略游戏', '生存建造', '11 bit studios', '11 bit studios', '2018-04-24', 118.00, '模拟经营,策略,生存,末日,单人'),
(56, '冰汽时代2', '模拟经营', '策略游戏', '生存建造', '11 bit studios', '11 bit studios', '2024-09-20', 168.00, '模拟经营,策略,生存,末日,单人'),
(57, '纪元1800', '模拟经营', '策略游戏', '沙盒创造', 'Ubisoft Mainz', 'Ubisoft', '2019-04-16', 198.00, '模拟经营,建造,工业,贸易,单人'),
(58, '庄园领主', '模拟经营', '策略游戏', '沙盒创造', 'Slavic Magic', 'Hooded Horse', '2024-04-26', 168.00, '模拟经营,中世纪,建造,策略,单人'),
(59, '都市：天际线', '模拟经营', '沙盒创造', '策略游戏', 'Colossal Order Ltd.', 'Paradox Interactive', '2015-03-10', 108.00, '模拟经营,城市建设,沙盒,策略,单人'),
(60, '都市：天际线2', '模拟经营', '沙盒创造', '策略游戏', 'Colossal Order Ltd.', 'Paradox Interactive', '2023-10-24', 218.00, '模拟经营,城市建设,沙盒,策略,单人'),

(61, '无人深空', '冒险游戏', '开放世界', '沙盒创造', 'Hello Games', 'Hello Games', '2016-08-12', 198.00, '开放世界,探索,太空,生存,单人'),
(62, '深海迷航', '冒险游戏', '生存建造', '开放世界', 'Unknown Worlds Entertainment', 'Unknown Worlds Entertainment', '2018-01-23', 108.00, '生存,海洋,探索,建造,单人'),
(63, '深海迷航：零度之下', '冒险游戏', '生存建造', '开放世界', 'Unknown Worlds Entertainment', 'Unknown Worlds Entertainment', '2021-05-14', 108.00, '生存,海洋,探索,建造,单人'),
(64, '潜水员戴夫', '冒险游戏', '模拟经营', '休闲游戏', 'MINTROCKET', 'MINTROCKET', '2023-06-28', 80.00, '休闲,经营,海洋,探索,单人'),
(65, '渔帆暗涌', '冒险游戏', '解谜探索', '休闲游戏', 'Black Salt Games', 'Team17', '2023-03-30', 110.00, '冒险,探索,克苏鲁,钓鱼,单人'),
(66, '超自然车旅', '冒险游戏', '生存建造', '解谜探索', 'Ironwood Studios', 'Kepler Interactive', '2024-02-22', 128.00, '冒险,驾驶,生存,探索,单人'),
(67, '死亡回归', '动作游戏', 'Roguelike肉鸽', '射击游戏', 'Housemarque', 'PlayStation Publishing LLC', '2023-02-15', 249.00, '动作,肉鸽,射击,科幻,单人'),
(68, '异星探险家', '冒险游戏', '沙盒创造', '生存建造', 'System Era Softworks', 'System Era Softworks', '2019-02-06', 108.00, '沙盒,太空,建造,探索,单人'),
(69, '木筏求生', '冒险游戏', '生存建造', '沙盒创造', 'Redbeet Interactive', 'Axolot Games', '2022-06-20', 68.00, '生存,建造,海洋,探索,单人'),
(70, '荒岛求生', '冒险游戏', '生存建造', '沙盒创造', 'Beam Team Games', 'Beam Team Publishing', '2020-08-10', 76.00, '生存,建造,海岛,探索,单人'),

(71, '哈迪斯', 'Roguelike肉鸽', '动作游戏', '角色扮演', 'Supergiant Games', 'Supergiant Games', '2020-09-17', 92.00, '肉鸽,动作,RPG,神话,单人'),
(72, '哈迪斯II', 'Roguelike肉鸽', '动作游戏', '角色扮演', 'Supergiant Games', 'Supergiant Games', '2025-09-25', 108.00, '肉鸽,动作,RPG,神话,单人'),
(73, '小丑牌', '卡牌构筑', 'Roguelike肉鸽', '策略游戏', 'LocalThunk', 'Playstack', '2024-02-20', 58.00, '卡牌,肉鸽,策略,扑克,单人'),
(74, '杀戮尖塔', '卡牌构筑', 'Roguelike肉鸽', '策略游戏', 'Mega Crit Games', 'Mega Crit Games', '2019-01-23', 80.00, '卡牌,肉鸽,策略,构筑,单人'),
(75, '吸血鬼幸存者', 'Roguelike肉鸽', '动作游戏', '休闲游戏', 'poncle', 'poncle', '2022-10-21', 25.00, '肉鸽,动作,休闲,割草,单人'),
(76, '死亡细胞', 'Roguelike肉鸽', '动作游戏', '平台跳跃', 'Motion Twin', 'Motion Twin', '2018-08-07', 80.00, '肉鸽,动作,平台跳跃,单人,像素'),
(77, '咩咩启示录', 'Roguelike肉鸽', '模拟经营', '动作游戏', 'Massive Monster', 'Devolver Digital', '2022-08-11', 92.00, '肉鸽,经营,动作,黑暗,单人'),
(78, '土豆兄弟', 'Roguelike肉鸽', '动作游戏', '休闲游戏', 'Blobfish', 'Blobfish', '2023-06-23', 22.00, '肉鸽,动作,休闲,割草,单人'),
(79, '挺进地牢', 'Roguelike肉鸽', '射击游戏', '动作游戏', 'Dodge Roll', 'Devolver Digital', '2016-04-05', 58.00, '肉鸽,射击,动作,地牢,单人'),
(80, '以撒的结合：重生', 'Roguelike肉鸽', '动作游戏', '休闲游戏', 'Nicalis, Inc.', 'Nicalis, Inc.', '2014-11-04', 50.00, '肉鸽,动作,休闲,地牢,单人'),

(81, '空洞骑士', '平台跳跃', '动作游戏', '解谜探索', 'Team Cherry', 'Team Cherry', '2017-02-24', 58.00, '平台跳跃,动作,探索,银河恶魔城,单人'),
(82, '空洞骑士：丝之歌', '平台跳跃', '动作游戏', '解谜探索', 'Team Cherry', 'Team Cherry', '2025-09-04', 98.00, '平台跳跃,动作,探索,银河恶魔城,单人'),
(83, '奥日与黑暗森林：终极版', '平台跳跃', '冒险游戏', '解谜探索', 'Moon Studios GmbH', 'Xbox Game Studios', '2016-04-27', 68.00, '平台跳跃,冒险,探索,剧情,单人'),
(84, '奥日与萤火意志', '平台跳跃', '冒险游戏', '解谜探索', 'Moon Studios GmbH', 'Xbox Game Studios', '2020-03-11', 90.00, '平台跳跃,冒险,探索,剧情,单人'),
(85, '蔚蓝', '平台跳跃', '休闲游戏', NULL, 'Extremely OK Games, Ltd.', 'Extremely OK Games, Ltd.', '2018-01-25', 76.00, '平台跳跃,独立,挑战,剧情,单人'),
(86, '茶杯头', '平台跳跃', '动作游戏', '休闲游戏', 'Studio MDHR Entertainment Inc.', 'Studio MDHR Entertainment Inc.', '2017-09-29', 78.00, '平台跳跃,动作,卡通,高难度,单人'),
(87, '动物井', '解谜探索', '平台跳跃', '冒险游戏', 'Shared Memory', 'Bigmode', '2024-05-09', 76.00, '解谜,平台跳跃,探索,独立,单人'),
(88, '渎神2', '平台跳跃', '魂like', '动作游戏', 'The Game Kitchen', 'Team17', '2023-08-24', 110.00, '平台跳跃,动作,魂like,黑暗奇幻,单人'),
(89, '九日', '平台跳跃', '动作游戏', '魂like', 'Red Candle Games', 'Red Candle Games', '2024-05-29', 128.00, '平台跳跃,动作,魂like,科幻,单人'),
(90, '突尼克', '冒险游戏', '解谜探索', '动作游戏', 'Andrew Shouldice', 'Finji', '2022-03-16', 110.00, '冒险,解谜,动作,探索,单人'),

(91, '生化危机4 重制版', '恐怖惊悚', '动作游戏', '剧情向', 'CAPCOM Co., Ltd.', 'CAPCOM Co., Ltd.', '2023-03-24', 198.00, '恐怖,动作,剧情,生存,单人'),
(92, '生化危机8：村庄', '恐怖惊悚', '动作游戏', '剧情向', 'CAPCOM Co., Ltd.', 'CAPCOM Co., Ltd.', '2021-05-07', 198.00, '恐怖,动作,剧情,生存,单人'),
(93, '生化危机2 重制版', '恐怖惊悚', '动作游戏', '剧情向', 'CAPCOM Co., Ltd.', 'CAPCOM Co., Ltd.', '2019-01-25', 148.00, '恐怖,动作,剧情,生存,单人'),
(94, '寂静岭2', '恐怖惊悚', '解谜探索', '剧情向', 'Bloober Team SA', 'KONAMI', '2024-10-08', 298.00, '恐怖,解谜,剧情,心理惊悚,单人'),
(95, '迷失', '冒险游戏', '解谜探索', '剧情向', 'BlueTwelve Studio', 'Annapurna Interactive', '2022-07-19', 110.00, '冒险,解谜,剧情,科幻,单人'),
(96, '底特律：化身为人', '剧情向', '冒险游戏', '视觉小说', 'Quantic Dream', 'Quantic Dream', '2020-06-18', 128.00, '剧情,互动电影,科幻,选择,单人'),
(97, '看火人', '冒险游戏', '剧情向', '解谜探索', 'Campo Santo', 'Panic', '2016-02-09', 76.00, '冒险,剧情,探索,叙事,单人'),
(98, '星际拓荒', '冒险游戏', '解谜探索', '剧情向', 'Mobius Digital', 'Annapurna Interactive', '2020-06-18', 92.00, '冒险,解谜,探索,太空,单人'),
(99, '活体脑细胞', '恐怖惊悚', '解谜探索', '剧情向', 'Frictional Games', 'Frictional Games', '2015-09-22', 90.00, '恐怖,解谜,剧情,科幻,单人'),
(100, '瘟疫传说：安魂曲', '冒险游戏', '剧情向', '动作游戏', 'Asobo Studio', 'Focus Entertainment', '2022-10-18', 188.00, '冒险,剧情,动作,潜行,单人');

DROP TEMPORARY TABLE IF EXISTS tmp_real_steam_category_map;
CREATE TEMPORARY TABLE tmp_real_steam_category_map AS
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
    CONCAT(
        '《', seed.game_name, '》是 Steam 上真实存在的热门单机游戏样本，按公开商店信息整理，主打 ',
        seed.primary_category_name, ' / ', seed.secondary_category_name,
        CASE
            WHEN seed.tertiary_category_name IS NULL THEN ''
            ELSE CONCAT(' / ', seed.tertiary_category_name)
        END,
        '，适合用于商城页面、搜索、推荐和分类筛选演示。'
    ) AS description_text,
    primary_category.category_id,
    seed.developer_name,
    seed.publisher_name,
    seed.release_date_value,
    seed.price_value,
    CASE
        WHEN seed.id <= 20 AND MOD(seed.id, 4) = 0 THEN ROUND(seed.price_value * 0.85, 2)
        WHEN seed.id BETWEEN 21 AND 60 AND MOD(seed.id, 5) = 0 THEN ROUND(seed.price_value * 0.80, 2)
        WHEN seed.price_value <= 120 AND MOD(seed.id, 3) = 0 THEN ROUND(seed.price_value * 0.90, 2)
        ELSE NULL
    END AS discount_price_value,
    CASE
        WHEN seed.primary_category_name IN ('动作游戏', '魂like') THEN 'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&h=450&fit=crop'
        WHEN seed.primary_category_name = '角色扮演' THEN 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800&h=450&fit=crop'
        WHEN seed.primary_category_name IN ('策略游戏', '模拟经营') THEN 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&h=450&fit=crop'
        WHEN seed.primary_category_name = '恐怖惊悚' THEN 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=800&h=450&fit=crop'
        ELSE 'https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=800&h=450&fit=crop'
    END AS image_url,
    CASE
        WHEN seed.price_value >= 350 THEN '推荐配置：Win10 / i7 / 16GB / RTX 3070 / 100GB'
        WHEN seed.price_value >= 250 THEN '推荐配置：Win10 / i5 / 16GB / RTX 2060 / 80GB'
        WHEN seed.price_value >= 150 THEN '推荐配置：Win10 / i5 / 16GB / GTX 1660 / 60GB'
        WHEN seed.price_value >= 90 THEN '推荐配置：Win10 / i5 / 8GB / GTX 1060 / 35GB'
        ELSE '推荐配置：Win10 / i3 / 8GB / 核显可运行 / 10GB'
    END AS requirements,
    seed.tags_text,
    CASE
        WHEN seed.id <= 20 THEN ROUND(4.68 + MOD(seed.id, 5) * 0.05, 2)
        WHEN seed.id <= 40 THEN ROUND(4.66 + MOD(seed.id, 5) * 0.05, 2)
        WHEN seed.id <= 60 THEN ROUND(4.60 + MOD(seed.id, 5) * 0.05, 2)
        WHEN seed.id <= 80 THEN ROUND(4.58 + MOD(seed.id, 5) * 0.06, 2)
        ELSE ROUND(4.56 + MOD(seed.id, 5) * 0.06, 2)
    END AS rating_value,
    CASE
        WHEN seed.id <= 20 THEN 65000 + seed.id * 8500
        WHEN seed.id <= 40 THEN 45000 + (seed.id - 20) * 7000
        WHEN seed.id <= 60 THEN 32000 + (seed.id - 40) * 5500
        WHEN seed.id <= 80 THEN 22000 + (seed.id - 60) * 4200
        ELSE 16000 + (seed.id - 80) * 3000
    END AS rating_count_value,
    CASE
        WHEN seed.id <= 20 THEN 380000 + seed.id * 42000
        WHEN seed.id <= 40 THEN 260000 + (seed.id - 20) * 32000
        WHEN seed.id <= 60 THEN 180000 + (seed.id - 40) * 24000
        WHEN seed.id <= 80 THEN 120000 + (seed.id - 60) * 16000
        ELSE 90000 + (seed.id - 80) * 11000
    END AS download_count_value,
    CASE
        WHEN seed.id <= 24 OR MOD(seed.id, 10) = 0 THEN TRUE
        ELSE FALSE
    END AS featured_value,
    'ACTIVE'
FROM tmp_real_steam_games seed
JOIN tmp_real_steam_category_map primary_category
    ON primary_category.name = seed.primary_category_name
LEFT JOIN games existing
    ON TRIM(existing.name) = TRIM(seed.game_name)
WHERE existing.id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT game_ref.id, category_ref.category_id
FROM tmp_real_steam_games mapping
JOIN games game_ref
    ON TRIM(game_ref.name) = TRIM(mapping.game_name)
JOIN tmp_real_steam_category_map category_ref
    ON category_ref.name = mapping.primary_category_name
LEFT JOIN game_categories gc
    ON gc.game_id = game_ref.id AND gc.category_id = category_ref.category_id
WHERE gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT game_ref.id, category_ref.category_id
FROM tmp_real_steam_games mapping
JOIN games game_ref
    ON TRIM(game_ref.name) = TRIM(mapping.game_name)
JOIN tmp_real_steam_category_map category_ref
    ON category_ref.name = mapping.secondary_category_name
LEFT JOIN game_categories gc
    ON gc.game_id = game_ref.id AND gc.category_id = category_ref.category_id
WHERE gc.game_id IS NULL;

INSERT INTO game_categories (game_id, category_id)
SELECT game_ref.id, category_ref.category_id
FROM tmp_real_steam_games mapping
JOIN games game_ref
    ON TRIM(game_ref.name) = TRIM(mapping.game_name)
JOIN tmp_real_steam_category_map category_ref
    ON category_ref.name = mapping.tertiary_category_name
LEFT JOIN game_categories gc
    ON gc.game_id = game_ref.id AND gc.category_id = category_ref.category_id
WHERE mapping.tertiary_category_name IS NOT NULL
  AND gc.game_id IS NULL;

DROP TEMPORARY TABLE IF EXISTS tmp_real_steam_category_map;
DROP TEMPORARY TABLE IF EXISTS tmp_real_steam_games;
