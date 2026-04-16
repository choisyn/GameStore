@echo off
echo 正在测试项目编译...
echo.

echo 1. 清理项目...
call mvn clean

echo.
echo 2. 编译项目...
call mvn compile

echo.
echo 3. 编译测试完成！
pause
