@echo off
echo 正在插入游戏数据...
mysql -u gamestore -p bishe < insert_games_data_simple.sql
echo 完成！
pause

