-- 为讨论广场帖子补充图片字段
-- 执行后可支持帖子内容插入图片

USE bishe;

SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'posts'
      AND COLUMN_NAME = 'images'
);

SET @alter_sql = IF(
    @column_exists = 0,
    'ALTER TABLE posts ADD COLUMN images TEXT NULL COMMENT ''帖子图片列表(JSON)'' AFTER content',
    'SELECT ''posts.images already exists'' AS message'
);

PREPARE stmt FROM @alter_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
