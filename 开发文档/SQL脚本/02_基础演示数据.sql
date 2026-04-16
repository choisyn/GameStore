-- 游戏社区商城系统
-- 游戏样本1（基础演示数据）
-- 适用场景：全新数据库或本地演示环境
USE bishe;

-- 鍒嗙被
INSERT INTO categories (name, description, sort_order, status)
SELECT '鍔ㄤ綔娓告垙', '浠ュ嵆鏃舵搷浣滃拰鎴樻枟浣撻獙涓轰富鐨勬父鎴?, 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '鍔ㄤ綔娓告垙');

INSERT INTO categories (name, description, sort_order, status)
SELECT '瑙掕壊鎵紨', '寮鸿皟鍓ф儏鎺ㄨ繘銆佹垚闀跨郴缁熶笌涓栫晫鎺㈢储', 2, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '瑙掕壊鎵紨');

INSERT INTO categories (name, description, sort_order, status)
SELECT '灏勫嚮娓告垙', 'FPS 鎴?TPS 鏂瑰悜鐨勫姩浣滃皠鍑荤被娓告垙', 3, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '灏勫嚮娓告垙');

INSERT INTO categories (name, description, sort_order, status)
SELECT '绛栫暐娓告垙', '寮鸿皟瑙勫垝銆佽祫婧愯皟搴﹀拰鎴樻湳鍗氬紙', 4, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '绛栫暐娓告垙');

INSERT INTO categories (name, description, sort_order, status)
SELECT '鍐掗櫓娓告垙', '寮鸿皟鎺㈢储銆佸墽鎯呭拰瑙ｈ皽浣撻獙', 5, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '鍐掗櫓娓告垙');

INSERT INTO categories (name, description, sort_order, status)
SELECT '浼戦棽娓告垙', '鑺傚杞绘澗銆侀€傚悎纰庣墖鍖栦綋楠岀殑娓告垙', 6, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '浼戦棽娓告垙');

SET @cat_action = (SELECT id FROM categories WHERE name = '鍔ㄤ綔娓告垙' LIMIT 1);
SET @cat_rpg = (SELECT id FROM categories WHERE name = '瑙掕壊鎵紨' LIMIT 1);
SET @cat_shoot = (SELECT id FROM categories WHERE name = '灏勫嚮娓告垙' LIMIT 1);
SET @cat_strategy = (SELECT id FROM categories WHERE name = '绛栫暐娓告垙' LIMIT 1);
SET @cat_adventure = (SELECT id FROM categories WHERE name = '鍐掗櫓娓告垙' LIMIT 1);
SET @cat_casual = (SELECT id FROM categories WHERE name = '浼戦棽娓告垙' LIMIT 1);

-- 鐢ㄦ埛
INSERT INTO users (username, email, password, role, status)
VALUES ('admin', 'admin@gamestore.com', 'admin', 'ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    password = VALUES(password),
    role = VALUES(role),
    status = VALUES(status);

INSERT INTO users (username, email, password, role, status)
VALUES ('testuser', 'test@example.com', '123456', 'USER', 'ACTIVE')
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    password = VALUES(password),
    role = VALUES(role),
    status = VALUES(status);

INSERT INTO users (username, email, password, role, status)
VALUES ('demo', 'demo@example.com', 'demo123', 'USER', 'ACTIVE')
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    password = VALUES(password),
    role = VALUES(role),
    status = VALUES(status);

SET @user_admin = (SELECT id FROM users WHERE username = 'admin' LIMIT 1);
SET @user_test = (SELECT id FROM users WHERE username = 'testuser' LIMIT 1);
SET @user_demo = (SELECT id FROM users WHERE username = 'demo' LIMIT 1);

-- 娓告垙
INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '璧涘崥鏈嬪厠2077',
    '鍦ㄥ涔嬪煄灞曞紑楂樿嚜鐢卞害鍐掗櫓鐨勫紑鏀句笘鐣?RPG銆?,
    @cat_rpg,
    'CD Projekt RED',
    'CD Projekt',
    '2020-12-10',
    298.00,
    NULL,
    'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&h=450&fit=crop',
    '鏈€浣庨厤缃細Win10 / i5 / 8GB / GTX 780 / 70GB',
    'RPG,寮€鏀句笘鐣?绉戝够,鍔ㄤ綔',
    4.20,
    15420,
    45000,
    TRUE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '璧涘崥鏈嬪厠2077');

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '宸笀3锛氱媯鐚?,
    '鍓ф儏涓庢帰绱㈠苟閲嶇殑缁忓吀濂囧够 RPG銆?,
    @cat_rpg,
    'CD Projekt RED',
    'CD Projekt',
    '2015-05-19',
    127.00,
    89.00,
    'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800&h=450&fit=crop',
    '鏈€浣庨厤缃細Win7 / i5 / 6GB / GTX 660 / 50GB',
    'RPG,寮€鏀句笘鐣?鍓ф儏,濂囧够',
    4.80,
    28350,
    62000,
    TRUE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '宸笀3锛氱媯鐚?);

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '鑹惧皵鐧绘硶鐜?,
    '榄傜郴鎴樻枟涓庡紑鏀炬帰绱㈢粨鍚堢殑楂樺己搴﹀姩浣?RPG銆?,
    @cat_rpg,
    'FromSoftware',
    'Bandai Namco',
    '2022-02-25',
    298.00,
    NULL,
    'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&h=450&fit=crop',
    '鎺ㄨ崘閰嶇疆锛歐in10 / i7 / 16GB / GTX 1070 / 60GB',
    'RPG,榄傜郴,寮€鏀句笘鐣?楂橀毦搴?,
    4.70,
    21000,
    53000,
    TRUE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '鑹惧皵鐧绘硶鐜?);

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '浣垮懡鍙敜锛氱幇浠ｆ垬浜塈II',
    '鑺傚绱у噾鐨勭幇浠ｅ啗浜嬮鏉愬皠鍑绘父鎴忋€?,
    @cat_shoot,
    'Infinity Ward',
    'Activision',
    '2023-11-10',
    469.00,
    NULL,
    'https://images.unsplash.com/photo-1511882150382-421056c89033?w=800&h=450&fit=crop',
    '鎺ㄨ崘閰嶇疆锛歐in10 / i7 / 16GB / RTX 2060 / 120GB',
    '灏勫嚮,鍐涗簨,澶氫汉,鍔ㄤ綔',
    4.10,
    9500,
    36000,
    TRUE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '浣垮懡鍙敜锛氱幇浠ｆ垬浜塈II');

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '鑽掗噹澶ч晼瀹細鏁戣祹2',
    '娌夋蹈寮忚タ閮ㄥ紑鏀句笘鐣屽啋闄╀綋楠屻€?,
    @cat_adventure,
    'Rockstar Games',
    'Rockstar Games',
    '2019-12-05',
    249.00,
    174.00,
    'https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=800&h=450&fit=crop',
    '鎺ㄨ崘閰嶇疆锛歐in10 / i7 / 12GB / GTX 1060 / 150GB',
    '寮€鏀句笘鐣?鍐掗櫓,鍓ф儏,瑗块儴',
    4.70,
    18000,
    40000,
    TRUE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '鑽掗噹澶ч晼瀹細鏁戣祹2');

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '鏂囨槑VI',
    '缁忓吀鍥炲悎鍒剁瓥鐣ヤ綔鍝侊紝寮鸿皟缁忚惀銆佺鎶€涓庢垬浜夎鍒掋€?,
    @cat_strategy,
    'Firaxis Games',
    '2K',
    '2016-10-21',
    199.00,
    79.00,
    'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&h=450&fit=crop',
    '鏈€浣庨厤缃細Win7 / i3 / 4GB / 1GB 鏄惧瓨 / 15GB',
    '绛栫暐,鍥炲悎鍒?缁忚惀,鍘嗗彶',
    4.50,
    8760,
    26000,
    FALSE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '鏂囨槑VI');

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    'Apex 鑻遍泟',
    '蹇妭濂忚嫳闆勬垬鏈珵鎶€灏勫嚮娓告垙銆?,
    @cat_shoot,
    'Respawn Entertainment',
    'EA',
    '2019-02-05',
    0.00,
    NULL,
    'https://images.unsplash.com/photo-1545239351-1141bd82e8a6?w=800&h=450&fit=crop',
    '鏈€浣庨厤缃細Win10 / i3 / 6GB / GTX 640 / 75GB',
    '灏勫嚮,绔炴妧,鍏嶈垂,澶氫汉',
    4.30,
    30200,
    98000,
    FALSE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = 'Apex 鑻遍泟');

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '鏃犵晱濂戠害',
    '鎴樻湳灏勫嚮涓庤嫳闆勬妧鑳界粨鍚堢殑绔炴妧浣滃搧銆?,
    @cat_shoot,
    'Riot Games',
    'Riot Games',
    '2020-06-02',
    0.00,
    NULL,
    'https://images.unsplash.com/photo-1560253023-3ec5d502959f?w=800&h=450&fit=crop',
    '鏈€浣庨厤缃細Win10 / i3 / 4GB / Intel HD 4000',
    '灏勫嚮,鎴樻湳,鍏嶈垂,绔炴妧',
    4.20,
    24000,
    76000,
    FALSE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '鏃犵晱濂戠害');

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '鍙嫾锛氬奖閫濅簩搴?,
    '寮鸿皟鎷涙灦銆佽妭濂忓拰鎸戞垬鎬х殑鍔ㄤ綔鍐掗櫓浣滃搧銆?,
    @cat_action,
    'FromSoftware',
    'Activision',
    '2019-03-22',
    268.00,
    NULL,
    'https://images.unsplash.com/photo-1510915228340-29c85a43dcfe?w=800&h=450&fit=crop',
    '鎺ㄨ崘閰嶇疆锛歐in10 / i5 / 8GB / GTX 970 / 25GB',
    '鍔ㄤ綔,楂橀毦搴?蹇嶈€?鍗曚汉',
    4.60,
    13200,
    31000,
    FALSE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '鍙嫾锛氬奖閫濅簩搴?);

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '瀵归┈宀涗箣榄?,
    '寮€鏀句笘鐣屾澹鏉愬姩浣滃啋闄╂父鎴忋€?,
    @cat_action,
    'Sucker Punch',
    'PlayStation Publishing',
    '2024-05-16',
    398.00,
    NULL,
    'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&h=450&fit=crop',
    '鎺ㄨ崘閰嶇疆锛歐in10 / i7 / 16GB / RTX 2060 / 75GB',
    '鍔ㄤ綔,寮€鏀句笘鐣?姝﹀＋,鍓ф儏',
    4.70,
    5800,
    12000,
    FALSE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '瀵归┈宀涗箣榄?);

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '鏄熼湶璋风墿璇?,
    '鍐滃満缁忚惀涓庣ぞ浜ゅ吇鎴愮浉缁撳悎鐨勪紤闂叉父鎴忋€?,
    @cat_casual,
    'ConcernedApe',
    'ConcernedApe',
    '2016-02-26',
    48.00,
    NULL,
    'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&h=450&fit=crop',
    '鏈€浣庨厤缃細Win7 / 2GB / 闆嗘樉鍗冲彲',
    '浼戦棽,缁忚惀,鍍忕礌,鍐滃満',
    4.90,
    40500,
    86000,
    FALSE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '鏄熼湶璋风墿璇?);

INSERT INTO games (
    name, description, category_id, developer, publisher, release_date,
    price, discount_price, image_url, system_requirements, tags, rating,
    rating_count, download_count, is_featured, status
)
SELECT
    '鍙屼汉鎴愯',
    '鍙屼汉鍗忎綔瑙ｈ皽涓庡姩浣滃啋闄╀綔鍝併€?,
    @cat_adventure,
    'Hazelight',
    'EA',
    '2021-03-26',
    198.00,
    99.00,
    'https://images.unsplash.com/photo-1520607162513-77705c0f0d4a?w=800&h=450&fit=crop',
    '鎺ㄨ崘閰嶇疆锛歐in10 / i5 / 8GB / GTX 980 / 50GB',
    '鍐掗櫓,鍚堜綔,瑙ｈ皽,鍙屼汉',
    4.80,
    16800,
    35000,
    FALSE,
    'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM games WHERE name = '鍙屼汉鎴愯');

-- 澶氬垎绫诲叧鑱旂ず渚?INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '瑙掕壊鎵紨'
WHERE g.name = '璧涘崥鏈嬪厠2077'
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '鍔ㄤ綔娓告垙'
WHERE g.name = '璧涘崥鏈嬪厠2077'
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '瑙掕壊鎵紨'
WHERE g.name = '宸笀3锛氱媯鐚?
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '鍐掗櫓娓告垙'
WHERE g.name = '宸笀3锛氱媯鐚?
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '鍔ㄤ綔娓告垙'
WHERE g.name = '鑹惧皵鐧绘硶鐜?
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '瑙掕壊鎵紨'
WHERE g.name = '鑹惧皵鐧绘硶鐜?
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '灏勫嚮娓告垙'
WHERE g.name = '浣垮懡鍙敜锛氱幇浠ｆ垬浜塈II'
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '鍔ㄤ綔娓告垙'
WHERE g.name = '浣垮懡鍙敜锛氱幇浠ｆ垬浜塈II'
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '绛栫暐娓告垙'
WHERE g.name = '鏂囨槑VI'
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '浼戦棽娓告垙'
WHERE g.name = '鏄熼湶璋风墿璇?
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

INSERT INTO game_categories (game_id, category_id)
SELECT g.id, c.id
FROM games g
JOIN categories c ON c.name = '鍐掗櫓娓告垙'
WHERE g.name = '鍙屼汉鎴愯'
  AND NOT EXISTS (
      SELECT 1 FROM game_categories gc
      WHERE gc.game_id = g.id AND gc.category_id = c.id
  );

-- 杞挱鍥?INSERT INTO banners (title, image_url, link_url, description, sort_order, is_active, type)
SELECT
    '娆㈣繋鏉ュ埌娓告垙绀惧尯鍟嗗煄',
    'https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=1920&h=600&fit=crop',
    '/#featured',
    '鎺㈢储鐑棬娓告垙涓庣ぞ鍖鸿璁哄唴瀹广€?,
    1,
    TRUE,
    'HOME'
WHERE NOT EXISTS (SELECT 1 FROM banners WHERE title = '娆㈣繋鏉ュ埌娓告垙绀惧尯鍟嗗煄');

INSERT INTO banners (title, image_url, link_url, description, sort_order, is_active, type)
SELECT
    '鏈懆鎺ㄨ崘娓告垙',
    'https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=1920&h=600&fit=crop',
    '/#games',
    '浠庡姩浣溿€丷PG 鍒扮瓥鐣ワ紝蹇€熸祻瑙堟湰鍛ㄧ儹闂ㄥ唴瀹广€?,
    2,
    TRUE,
    'HOME'
WHERE NOT EXISTS (SELECT 1 FROM banners WHERE title = '鏈懆鎺ㄨ崘娓告垙');

INSERT INTO banners (title, image_url, link_url, description, sort_order, is_active, type)
SELECT
    '绀惧尯璁ㄨ绮鹃€?,
    'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=1920&h=600&fit=crop',
    '/community',
    '鏌ョ湅鐜╁鏀荤暐銆佺粍闃熷拰璁ㄨ鍐呭銆?,
    3,
    TRUE,
    'HOME'
WHERE NOT EXISTS (SELECT 1 FROM banners WHERE title = '绀惧尯璁ㄨ绮鹃€?);

-- 绀惧尯鏉垮潡
INSERT INTO community_sections (name, icon, description, sort_order, status)
SELECT '璁ㄨ骞垮満', 'bi-chat-dots', '鑷敱璁ㄨ娓告垙璇濋', 1, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM community_sections WHERE name = '璁ㄨ骞垮満');

INSERT INTO community_sections (name, icon, description, sort_order, status)
SELECT '娓告垙缁勯槦', 'bi-people', '瀵绘壘闃熷弸缁勯槦寮€榛?, 2, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM community_sections WHERE name = '娓告垙缁勯槦');

INSERT INTO community_sections (name, icon, description, sort_order, status)
SELECT '娓告垙鍒嗕韩', 'bi-share', '鍒嗕韩鎴浘銆佽棰戜笌楂樺厜鏃跺埢', 3, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM community_sections WHERE name = '娓告垙鍒嗕韩');

INSERT INTO community_sections (name, icon, description, sort_order, status)
SELECT '娓告垙鏀荤暐', 'bi-book', '鍙戝竷蹇冨緱銆佹敾鐣ヤ笌鏁欏', 4, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM community_sections WHERE name = '娓告垙鏀荤暐');

INSERT INTO community_sections (name, icon, description, sort_order, status)
SELECT '鍒涙剰宸ュ潑', 'bi-brush', '灞曠ず MOD銆佸湴鍥惧拰鍒涙剰浣滃搧', 5, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM community_sections WHERE name = '鍒涙剰宸ュ潑');

SET @section_forum = (SELECT id FROM community_sections WHERE name = '璁ㄨ骞垮満' LIMIT 1);
SET @section_team = (SELECT id FROM community_sections WHERE name = '娓告垙缁勯槦' LIMIT 1);

-- 璁ㄨ骞垮満绀轰緥
INSERT INTO posts (title, content, user_id, game_id, category, status)
SELECT
    '璧涘崥鏈嬪厠2077 鐜板湪杩樺€煎緱鍏ュ潙鍚楋紵',
    '濡傛灉浣犳洿鐪嬮噸鍓ф儏鍜屼笘鐣岃锛岀幇鍦ㄥ叆鍧戜緷鐒舵槸寰堝ソ鐨勯€夋嫨銆?,
    @user_test,
    (SELECT id FROM games WHERE name = '璧涘崥鏈嬪厠2077' LIMIT 1),
    '娓告垙璇勬祴',
    'PUBLISHED'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '璧涘崥鏈嬪厠2077 鐜板湪杩樺€煎緱鍏ュ潙鍚楋紵');

INSERT INTO posts (title, content, user_id, game_id, category, status)
SELECT
    '鏂囨槑VI 鏂版墜寮€灞€鎬濊矾',
    '浼樺厛閾哄煄鍜岃祫婧愯妭濂忥紝姣斾竴鍛冲啿绉戞妧鏇寸ǔ瀹氥€?,
    @user_demo,
    (SELECT id FROM games WHERE name = '鏂囨槑VI' LIMIT 1),
    '鏀荤暐鎸囧崡',
    'PUBLISHED'
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = '鏂囨槑VI 鏂版墜寮€灞€鎬濊矾');

SET @post_cp = (SELECT id FROM posts WHERE title = '璧涘崥鏈嬪厠2077 鐜板湪杩樺€煎緱鍏ュ潙鍚楋紵' LIMIT 1);
SET @post_civ = (SELECT id FROM posts WHERE title = '鏂囨槑VI 鏂版墜寮€灞€鎬濊矾' LIMIT 1);

INSERT INTO comments (post_id, user_id, content, status)
SELECT @post_cp, @user_demo, '鍓ф儏鍜岀編鏈緷鐒跺緢寮猴紝鎺ㄨ崘鎵撳畬涓荤嚎鍐嶅埛鏀嚎銆?, 'PUBLISHED'
WHERE @post_cp IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM comments
      WHERE post_id = @post_cp AND user_id = @user_demo
        AND content = '鍓ф儏鍜岀編鏈緷鐒跺緢寮猴紝鎺ㄨ崘鎵撳畬涓荤嚎鍐嶅埛鏀嚎銆?
  );

INSERT INTO comments (post_id, user_id, content, status)
SELECT @post_civ, @user_test, '鍓嶆湡鍒妸鍖哄煙閾哄お鏁ｏ紝骞哥搴﹀鏄撶偢銆?, 'PUBLISHED'
WHERE @post_civ IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM comments
      WHERE post_id = @post_civ AND user_id = @user_test
        AND content = '鍓嶆湡鍒妸鍖哄煙閾哄お鏁ｏ紝骞哥搴﹀鏄撶偢銆?
  );

-- 鍟嗗煄闂幆婕旂ず鏁版嵁
INSERT INTO orders (order_no, user_id, total_amount, payable_amount, points_earned, status, payment_method, paid_at)
SELECT 'GSDEMO20260001', @user_demo, 48.00, 48.00, 48, 'PAID', 'MOCK', NOW()
WHERE @user_demo IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM orders WHERE order_no = 'GSDEMO20260001');

SET @order_demo_paid = (SELECT id FROM orders WHERE order_no = 'GSDEMO20260001' LIMIT 1);
SET @game_stardew = (SELECT id FROM games WHERE name = '鏄熼湶璋风墿璇? LIMIT 1);
SET @game_ghost = (SELECT id FROM games WHERE name = '瀵归┈宀涗箣榄? LIMIT 1);

INSERT INTO order_items (order_id, game_id, game_name, game_image_url, unit_price, quantity, subtotal)
SELECT
    @order_demo_paid,
    g.id,
    g.name,
    g.image_url,
    48.00,
    1,
    48.00
FROM games g
WHERE @order_demo_paid IS NOT NULL
  AND g.id = @game_stardew
  AND NOT EXISTS (
      SELECT 1 FROM order_items
      WHERE order_id = @order_demo_paid AND game_id = g.id
  );

INSERT INTO user_games (user_id, game_id, order_id, acquired_price)
SELECT @user_demo, @game_stardew, @order_demo_paid, 48.00
WHERE @user_demo IS NOT NULL
  AND @game_stardew IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM user_games
      WHERE user_id = @user_demo AND game_id = @game_stardew
  );

INSERT INTO point_transactions (user_id, change_amount, balance_after, type, description, order_id)
SELECT @user_demo, 48, 48, 'EARN', '璁㈠崟 GSDEMO20260001 鏀粯瀹屾垚锛岃幏寰楃Н鍒?, @order_demo_paid
WHERE @user_demo IS NOT NULL
  AND @order_demo_paid IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM point_transactions
      WHERE user_id = @user_demo AND order_id = @order_demo_paid
  );

UPDATE users
SET points = 48
WHERE id = @user_demo
  AND (points IS NULL OR points < 48);

INSERT INTO cart_items (user_id, game_id, quantity, unit_price, selected)
SELECT @user_test, @game_ghost, 1, 398.00, TRUE
WHERE @user_test IS NOT NULL
  AND @game_ghost IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM cart_items
      WHERE user_id = @user_test AND game_id = @game_ghost
  );

-- 绀惧尯鏉垮潡绀轰緥
INSERT INTO community_posts (section_id, user_id, title, content, status)
SELECT
    @section_forum,
    @user_admin,
    '娆㈣繋鏉ュ埌绀惧尯鏉垮潡',
    '杩欓噷鐢ㄤ簬鍙戝竷绀惧尯鍏憡銆佹椿鍔ㄥ拰绮鹃€夊唴瀹广€?,
    'PUBLISHED'
WHERE @section_forum IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM community_posts WHERE title = '娆㈣繋鏉ュ埌绀惧尯鏉垮潡');

INSERT INTO community_posts (section_id, user_id, title, content, status)
SELECT
    @section_team,
    @user_test,
    '鍛ㄦ湯涓€璧峰紑榛戯紝鎵?2 鍚嶉槦鍙?,
    '涓昏鐜╁皠鍑诲拰鍚堜綔闂叧锛屾櫄涓?8 鐐瑰悗鍦ㄧ嚎銆?,
    'PUBLISHED'
WHERE @section_team IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM community_posts WHERE title = '鍛ㄦ湯涓€璧峰紑榛戯紝鎵?2 鍚嶉槦鍙?);

SET @community_post_notice = (SELECT id FROM community_posts WHERE title = '娆㈣繋鏉ュ埌绀惧尯鏉垮潡' LIMIT 1);
SET @community_post_team = (SELECT id FROM community_posts WHERE title = '鍛ㄦ湯涓€璧峰紑榛戯紝鎵?2 鍚嶉槦鍙? LIMIT 1);

INSERT INTO community_comments (post_id, user_id, content, status)
SELECT @community_post_notice, @user_demo, '鏀跺埌锛屽悗闈㈡椿鍔ㄥ彲浠ュ彂鍦ㄨ繖閲屻€?, 'PUBLISHED'
WHERE @community_post_notice IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM community_comments
      WHERE post_id = @community_post_notice AND user_id = @user_demo
        AND content = '鏀跺埌锛屽悗闈㈡椿鍔ㄥ彲浠ュ彂鍦ㄨ繖閲屻€?
  );

INSERT INTO community_comments (post_id, user_id, content, status)
SELECT @community_post_team, @user_admin, '鍙互锛屽缓璁『渚垮啓涓婃搮闀夸綅缃拰娓告垙骞冲彴銆?, 'PUBLISHED'
WHERE @community_post_team IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM community_comments
      WHERE post_id = @community_post_team AND user_id = @user_admin
        AND content = '鍙互锛屽缓璁『渚垮啓涓婃搮闀夸綅缃拰娓告垙骞冲彴銆?
  );
