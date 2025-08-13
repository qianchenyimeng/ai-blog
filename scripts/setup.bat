@echo off
REM 个人博客系统Windows环境设置脚本

echo 设置个人博客系统...

REM 创建必要的目录
if not exist "logs" mkdir logs
if not exist "uploads" mkdir uploads
if not exist "config" mkdir config

REM 设置环境变量
set JAVA_HOME=%JAVA_HOME%
if "%JAVA_HOME%"=="" (
    echo 警告: JAVA_HOME 环境变量未设置
    echo 请设置 JAVA_HOME 指向您的 Java 安装目录
)

REM 检查Java版本
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: Java 未安装或未在 PATH 中
    pause
    exit /b 1
)

REM 检查Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo 错误: Maven 未安装或未在 PATH 中
    pause
    exit /b 1
)

echo 环境设置完成！
echo.
echo 使用方法:
echo   开发环境: scripts\start.bat dev
echo   测试环境: scripts\start.bat test  
echo   生产环境: scripts\start.bat prod
echo.
pause