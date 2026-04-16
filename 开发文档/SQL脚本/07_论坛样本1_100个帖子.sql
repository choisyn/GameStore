USE bishe;

-- =========================================================
-- 论坛样本1：新增 100 个讨论广场帖子
-- 说明：
-- 1. 通过 10 个主题方向 × 10 个讨论角度 组合生成 100 个帖子
-- 2. 帖子类型尽量分散，部分帖子关联具体游戏，部分作为泛讨论贴
-- 3. 可直接用于 forum.html / /api/posts 的演示
-- =========================================================

SET @fallback_user = (SELECT id FROM users ORDER BY id LIMIT 1);
SET @user_admin = COALESCE((SELECT id FROM users WHERE username = 'admin' LIMIT 1), @fallback_user);
SET @user_test = COALESCE((SELECT id FROM users WHERE username = 'testuser' LIMIT 1), @fallback_user);
SET @user_demo = COALESCE((SELECT id FROM users WHERE username = 'demo' LIMIT 1), @fallback_user);

DROP TEMPORARY TABLE IF EXISTS tmp_forum_game_pool;
SET @forum_game_rownum := 0;
CREATE TEMPORARY TABLE tmp_forum_game_pool AS
SELECT
    (@forum_game_rownum := @forum_game_rownum + 1) AS rn,
    g.id,
    g.name
FROM (
    SELECT id, name
    FROM games
    WHERE status = 'ACTIVE'
    ORDER BY created_at DESC, id DESC
    LIMIT 20
) g;

DROP TEMPORARY TABLE IF EXISTS tmp_forum_topics;
CREATE TEMPORARY TABLE tmp_forum_topics (
    id INT PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL,
    topic_focus VARCHAR(60) NOT NULL,
    body_intro VARCHAR(220) NOT NULL,
    is_general TINYINT(1) NOT NULL DEFAULT 0
) ENGINE=Memory;

INSERT INTO tmp_forum_topics VALUES
(1, '攻略交流', '开荒节奏', '这类帖子更偏向实战经验分享，适合整理流程、路线和资源分配建议。', 0),
(2, '新手求助', '入坑问题', '这类帖子更适合收集新手常见疑问，帮助刚接触游戏的玩家快速上手。', 0),
(3, '配置优化', '画面与帧率', '这类帖子聚焦于硬件配置、画面选项和稳定帧率的平衡方案。', 0),
(4, '剧情讨论', '叙事理解', '这类帖子适合讨论人物关系、叙事伏笔以及结局理解。', 0),
(5, '联机组队', '合作体验', '这类帖子适合寻找队友、讨论联机配合与语音沟通节奏。', 0),
(6, '折扣情报', '购买建议', '这类帖子更偏向价格讨论、版本选择和活动入手时机。', 1),
(7, 'DLC评测', '拓展内容', '这类帖子适合讨论 DLC、季票和额外内容是否值得投入。', 0),
(8, '成就挑战', '高难目标', '这类帖子适合整理成就路线、全收集规划和极限挑战心得。', 0),
(9, 'MOD分享', '创意玩法', '这类帖子适合交流 MOD、创意工坊和扩展玩法搭配。', 0),
(10, '世界观杂谈', '设定考据', '这类帖子更适合讨论题材设定、背景世界和风格灵感来源。', 1);

DROP TEMPORARY TABLE IF EXISTS tmp_forum_angles;
CREATE TEMPORARY TABLE tmp_forum_angles (
    id INT PRIMARY KEY,
    title_suffix VARCHAR(80) NOT NULL,
    angle_text VARCHAR(220) NOT NULL
) ENGINE=Memory;

INSERT INTO tmp_forum_angles VALUES
(1, '开荒路线怎么安排更顺？', '我想重点聊聊前期路线推进时，哪些任务、资源和功能应该优先处理。'),
(2, '流派搭配有没有更稳的思路？', '如果从长期养成角度看，哪些流派组合更容易兼顾体验和效率。'),
(3, '新手第一周最容易踩哪些坑？', '想把容易忽略的机制、资源误区和节奏问题提前整理出来。'),
(4, '画面设置和帧率应该怎么平衡？', '更关心实际体验层面的设置思路，而不是单纯追求参数最大化。'),
(5, '隐藏内容有哪些值得专门去找？', '想优先讨论不容易发现、但很值得体验的支线与隐藏内容。'),
(6, '地图探索时哪些区域最容易错过？', '希望大家补充那些容易被跳过但回报很高的探索点位。'),
(7, '版本更新后体验变化大吗？', '如果你最近重新回坑，想看看这段时间版本改动带来了哪些真实变化。'),
(8, '中后期节奏会不会突然掉下来？', '我比较关注中后期的重复度、成长反馈和内容密度变化。'),
(9, '单人玩还是联机玩更有意思？', '如果同一个内容既能单人也能多人，想比较两种节奏的差别。'),
(10, '现在这个阶段还值不值得入手？', '结合内容量、价格和社区活跃度，想听听大家更实际的购买建议。');

DROP TEMPORARY TABLE IF EXISTS tmp_forum_posts;
CREATE TEMPORARY TABLE tmp_forum_posts AS
SELECT
    ((topic.id - 1) * 10 + angle.id) AS seq_no,
    CONCAT(
        '【', topic.category_name, '】',
        CASE
            WHEN topic.is_general = 1 THEN topic.topic_focus
            ELSE COALESCE(game_pool.name, topic.topic_focus)
        END,
        angle.title_suffix
    ) AS title_text,
    CONCAT(
        topic.body_intro, '\n\n',
        '讨论主题：',
        CASE
            WHEN topic.is_general = 1 THEN topic.topic_focus
            ELSE COALESCE(game_pool.name, topic.topic_focus)
        END,
        '。\n',
        angle.angle_text,
        ' 欢迎大家从自己的游玩时长、设备配置、流派选择或购买时机出发，补充更具体的体验和建议。'
    ) AS content_text,
    CASE WHEN topic.is_general = 1 THEN NULL ELSE game_pool.id END AS game_id_value,
    topic.category_name AS category_value,
    CASE MOD(topic.id + angle.id, 3)
        WHEN 0 THEN @user_admin
        WHEN 1 THEN @user_test
        ELSE @user_demo
    END AS user_id_value,
    26 + MOD(topic.id * 17 + angle.id * 11, 260) AS view_count_value,
    4 + MOD(topic.id * 7 + angle.id * 5, 58) AS like_count_value,
    MOD(topic.id * 3 + angle.id * 2, 18) AS comment_count_value,
    CASE WHEN topic.id = 1 AND angle.id <= 2 THEN TRUE ELSE FALSE END AS pinned_value,
    CASE WHEN MOD(topic.id * 10 + angle.id, 9) = 0 THEN TRUE ELSE FALSE END AS featured_value,
    DATE_SUB(NOW(), INTERVAL (170 - ((topic.id - 1) * 10 + angle.id)) DAY) AS created_at_value,
    DATE_ADD(
        DATE_SUB(NOW(), INTERVAL (170 - ((topic.id - 1) * 10 + angle.id)) DAY),
        INTERVAL MOD(topic.id + angle.id, 5) DAY
    ) AS last_comment_at_value
FROM tmp_forum_topics topic
CROSS JOIN tmp_forum_angles angle
LEFT JOIN tmp_forum_game_pool game_pool
    ON game_pool.rn = MOD(topic.id + angle.id - 2, 20) + 1;

INSERT INTO posts (
    title,
    content,
    user_id,
    game_id,
    category,
    view_count,
    like_count,
    comment_count,
    is_pinned,
    is_featured,
    status,
    created_at,
    updated_at,
    last_comment_at
)
SELECT
    seed.title_text,
    seed.content_text,
    seed.user_id_value,
    seed.game_id_value,
    seed.category_value,
    seed.view_count_value,
    seed.like_count_value,
    seed.comment_count_value,
    seed.pinned_value,
    seed.featured_value,
    'PUBLISHED',
    seed.created_at_value,
    seed.created_at_value,
    seed.last_comment_at_value
FROM tmp_forum_posts seed
LEFT JOIN posts existing
    ON existing.title = seed.title_text
WHERE existing.id IS NULL;

DROP TEMPORARY TABLE IF EXISTS tmp_forum_posts;
DROP TEMPORARY TABLE IF EXISTS tmp_forum_angles;
DROP TEMPORARY TABLE IF EXISTS tmp_forum_topics;
DROP TEMPORARY TABLE IF EXISTS tmp_forum_game_pool;
