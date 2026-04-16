-- 游戏攻略系统示例数据
-- 说明：如果你的数据库里没有对应游戏名，可以把下面的游戏名改成你当前库里已有的名称
USE bishe;

SET @user_admin = (SELECT id FROM users WHERE username = 'admin' LIMIT 1);
SET @user_demo = (SELECT id FROM users WHERE username = 'demo' LIMIT 1);
SET @user_test = (SELECT id FROM users WHERE username = 'testuser' LIMIT 1);

SET @game_wukong = (SELECT id FROM games WHERE name = '黑神话：悟空' LIMIT 1);
SET @game_elden = (SELECT id FROM games WHERE name = '艾尔登法环' LIMIT 1);
SET @game_bg3 = (SELECT id FROM games WHERE name = '博德之门3' LIMIT 1);
SET @game_sekiro = (SELECT id FROM games WHERE name = '只狼：影逝二度 年度版' LIMIT 1);

INSERT INTO game_guides (
    game_id, author_id, title, summary, content, cover_image_url,
    difficulty, estimated_minutes, tags, view_count, like_count,
    is_featured, status, published_at, created_at, updated_at
)
SELECT
    @game_wukong,
    COALESCE(@user_admin, @user_demo, @user_test),
    '《黑神话：悟空》第一章开荒路线与Boss准备清单',
    '适合第一次上手玩家的章节推进攻略，包含装备准备、资源分配和关键 Boss 前注意事项。',
    '一、开局优先目标
先熟悉轻击、重击、闪避和定身术的衔接，不要急着追求高连段，先把容错打出来。

二、资源怎么花
前期优先把提升生存和稳定输出的天赋点出来，丹药和恢复类资源不要囤着不用。

三、Boss 前准备
进入精英怪和 Boss 战前，先确认法力、葫芦次数和技能冷却。遇到连续攻击型敌人时，以闪避反击为主，不要贪刀。

四、推荐思路
如果连续失败，可以先回头清理支线和小怪，把等级、材料和手感补足后再回来挑战。',
    'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=1200&h=600&fit=crop',
    'BEGINNER',
    18,
    '黑神话悟空,开荒,Boss,新手',
    126,
    34,
    TRUE,
    'PUBLISHED',
    NOW(),
    NOW(),
    NOW()
WHERE @game_wukong IS NOT NULL
  AND COALESCE(@user_admin, @user_demo, @user_test) IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM game_guides WHERE title = '《黑神话：悟空》第一章开荒路线与Boss准备清单'
  );

INSERT INTO game_guides (
    game_id, author_id, title, summary, content, cover_image_url,
    difficulty, estimated_minutes, tags, view_count, like_count,
    is_featured, status, published_at, created_at, updated_at
)
SELECT
    @game_elden,
    COALESCE(@user_demo, @user_admin, @user_test),
    '《艾尔登法环》近战流前期加点与地图推进建议',
    '面向近战玩家的入门攻略，帮助你少走弯路地完成前期升级、武器和地图探索。',
    '一、属性分配
前期优先生命与耐力，保证容错和翻滚次数，再根据武器补力量或灵巧。

二、探索节奏
不要只盯着主线 Boss，优先把周边地下墓地、教堂和营地清一遍，收益通常更高。

三、武器与强化
找到顺手的近战武器后尽快强化，稳定强化比频繁换武器更能提升体验。

四、战斗建议
面对大体型敌人时多观察收招，不要连续翻滚耗空精力，留一段体力做应急。',
    'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=1200&h=600&fit=crop',
    'BEGINNER',
    20,
    '艾尔登法环,近战,加点,地图探索',
    98,
    29,
    TRUE,
    'PUBLISHED',
    NOW(),
    NOW(),
    NOW()
WHERE @game_elden IS NOT NULL
  AND COALESCE(@user_demo, @user_admin, @user_test) IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM game_guides WHERE title = '《艾尔登法环》近战流前期加点与地图推进建议'
  );

INSERT INTO game_guides (
    game_id, author_id, title, summary, content, cover_image_url,
    difficulty, estimated_minutes, tags, view_count, like_count,
    is_featured, status, published_at, created_at, updated_at
)
SELECT
    @game_bg3,
    COALESCE(@user_test, @user_admin, @user_demo),
    '《博德之门3》第一章队伍搭配与资源管理思路',
    '给刚接触 CRPG 的玩家一个更稳妥的思路，重点放在角色分工、补给和战斗前准备。',
    '一、队伍分工
建议至少保证前排承伤、稳定输出、治疗/辅助和功能位四种角色定位。

二、长休与短休
不要把资源全部堆到一场战斗中，学会利用短休、地形和先手布置来减少消耗。

三、战斗前准备
开打前先看敌人站位，能潜行、能高打低、能分批引怪就不要正面硬冲。

四、剧情与探索
第一章很多奖励都藏在对话、支线和环境互动里，多存档、多尝试通常会更有收获。',
    'https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=1200&h=600&fit=crop',
    'INTERMEDIATE',
    22,
    '博德之门3,CRPG,队伍搭配,资源管理',
    72,
    18,
    FALSE,
    'PUBLISHED',
    NOW(),
    NOW(),
    NOW()
WHERE @game_bg3 IS NOT NULL
  AND COALESCE(@user_test, @user_admin, @user_demo) IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM game_guides WHERE title = '《博德之门3》第一章队伍搭配与资源管理思路'
  );

INSERT INTO game_guides (
    game_id, author_id, title, summary, content, cover_image_url,
    difficulty, estimated_minutes, tags, view_count, like_count,
    is_featured, status, published_at, created_at, updated_at
)
SELECT
    @game_sekiro,
    COALESCE(@user_admin, @user_test, @user_demo),
    '《只狼》弹反节奏训练法与危险攻击处理技巧',
    '适合已经入门但总是被打乱节奏的玩家，重点练习弹反、识别危与处理压制战。',
    '一、先练什么
先练稳定识别敌人的前摇，再练固定节奏弹反，不要一开始就追求极限压刀。

二、危险攻击怎么判断
突刺优先识别踩刀机会，横扫多跳躲，投技要拉开距离或提前绕背。

三、练习方式
找一个熟悉的精英怪反复练，不看输赢，只看自己是否准确做出了想要的动作。

四、心态建议
只狼更像节奏游戏，很多失败并不是属性不够，而是节奏识别还没建立起来。',
    'https://images.unsplash.com/photo-1511882150382-421056c89033?w=1200&h=600&fit=crop',
    'ADVANCED',
    16,
    '只狼,弹反,节奏,Boss',
    61,
    16,
    FALSE,
    'PUBLISHED',
    NOW(),
    NOW(),
    NOW()
WHERE @game_sekiro IS NOT NULL
  AND COALESCE(@user_admin, @user_test, @user_demo) IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM game_guides WHERE title = '《只狼》弹反节奏训练法与危险攻击处理技巧'
  );

-- 查看导入结果
-- SELECT id, title, difficulty, is_featured, published_at FROM game_guides ORDER BY id DESC;
