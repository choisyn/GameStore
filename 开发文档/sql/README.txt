数据库导出说明

可直接使用的文件：
1. bishe_full_notablespaces_20260416_083830.sql  -> 完整备份（包含建库/建表/数据）
2. bishe_data_only_notablespaces_20260416_083830.sql  -> 仅数据备份（目标库已建好表时使用）
3. bishe_table_counts_20260416_083800.tsv -> 当前表数量统计

建议迁移方式：
1. 在目标设备安装 MySQL 8.x。
2. 如果目标设备还没有 bishe 库，优先导入完整备份。
3. 如果目标设备已经有相同表结构，只想覆盖数据，则导入仅数据备份。

Windows 导入示例（推荐在 cmd 中执行）：
1. 完整导入：
   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u 用户名 -p < bishe_full_notablespaces_20260416_083830.sql
2. 仅导入数据：
   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u 用户名 -p bishe < bishe_data_only_notablespaces_20260416_083830.sql

如果你在 PowerShell 里操作，可以先切到导出目录后执行：
1. cmd /c "\"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe\" -u 用户名 -p < bishe_full_notablespaces_20260416_083830.sql"
2. cmd /c "\"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe\" -u 用户名 -p bishe < bishe_data_only_notablespaces_20260416_083830.sql"

注意：
- 本次可用备份文件名里包含 notablespaces，这是为了绕过普通账号缺少 PROCESS 权限导致的 mysqldump tablespace 错误。
- 目录中较早生成但文件名不含 notablespaces 的 SQL 文件请忽略，不作为最终可用备份。
- 导入前建议确认目标库字符集为 utf8mb4。
- 备份中包含用户表数据，请按正式业务数据处理，不要随意外传。
