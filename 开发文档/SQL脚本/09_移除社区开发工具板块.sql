USE bishe;

-- =========================================================
-- 移除社区“开发工具”板块
-- 说明：
-- 1. 删除 community_sections 中名为“开发工具”的板块
-- 2. 依赖外键级联，相关 community_posts / community_comments 会一并删除
-- 3. 执行后该板块不会再出现在社区首页、首页社区菜单和后台社区管理里
-- =========================================================

START TRANSACTION;

DELETE FROM community_sections
WHERE TRIM(name) = '开发工具';

COMMIT;
