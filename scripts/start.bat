@echo off
REM 个人博客系统Windows启动脚本

setlocal enabledelayedexpansion

REM 默认配置
set PROFILE=dev
set DEBUG_MODE=false
set JAR_FILE=
set JAVA_OPTS=

REM 解析命令行参数
:parse_args
if "%1"=="" goto :check_env
if "%1"=="dev" (
    set PROFILE=dev
    shift
    goto :parse_args
)
if "%1"=="test" (
    set PROFILE=test
    shift
    goto :parse_args
)
if "%1"=="prod" (
    set PROFILE=prod
    shift
    goto :parse_args
)
if "%1"=="--debug" (
    set DEBUG_MODE=true
    shift
    goto :parse_args
)
if "%1"=="-h" goto :show_help
if "%1"=="--help" goto :show_help

echo 未知参数: %1
goto :show_help

:show_help
echo 使用方法: %0 [环境] [选项]
echo 环境:
echo   dev     开发环境 (默认)
echo   test    测试环境
echo   prod    生产环境
echo 选项:
echo   --debug 启用调试模式
echo   -h, --help 显示帮助信息
exit /b 0

:check_env
echo [INFO] 启动个人博客系统...
echo [INFO] 环境: %PROFILE%

REM 检查Java环境
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java未安装或未在PATH中
    pause
    exit /b 1
)

REM 查找JAR文件
for %%f in (target\*.jar) do (
    if not "%%f"=="target\*-sources.jar" (
        if not "%%f"=="target\*-javadoc.jar" (
            set JAR_FILE=%%f
            goto :found_jar
        )
    )
)

echo [ERROR] 未找到JAR文件，请先运行 mvn clean package
pause
exit /b 1

:found_jar
echo [INFO] 找到JAR文件: %JAR_FILE%

REM 创建必要的目录
if not exist "logs" mkdir logs
if not exist "uploads" mkdir uploads

REM 设置JVM参数
if "%PROFILE%"=="dev" (
    set JAVA_OPTS=-Xms512m -Xmx1g -XX:+UseG1GC
)
if "%PROFILE%"=="test" (
    set JAVA_OPTS=-Xms256m -Xmx512m -XX:+UseG1GC
)
if "%PROFILE%"=="prod" (
    set JAVA_OPTS=-Xms1g -Xmx2g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=logs
)

REM 调试模式
if "%DEBUG_MODE%"=="true" (
    set JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
    echo [INFO] 调试模式已启用，调试端口: 5005
)

REM 添加JVM监控参数
set JAVA_OPTS=%JAVA_OPTS% -Djava.awt.headless=true
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set JAVA_OPTS=%JAVA_OPTS% -Duser.timezone=Asia/Shanghai

REM 设置环境变量
set SPRING_PROFILES_ACTIVE=%PROFILE%

REM 启动应用
echo [INFO] 启动命令: java %JAVA_OPTS% -jar %JAR_FILE% --spring.profiles.active=%PROFILE%
echo [INFO] 按 Ctrl+C 停止应用

java %JAVA_OPTS% -jar %JAR_FILE% --spring.profiles.active=%PROFILE%

pause