@echo off
REM 个人博客系统Windows停止脚本

echo [INFO] 停止个人博客系统...

REM 查找Java进程
for /f "tokens=2" %%i in ('tasklist /fi "imagename eq java.exe" /fo csv ^| find "java.exe"') do (
    set PID=%%i
    set PID=!PID:"=!
    
    REM 检查是否是博客应用进程
    for /f "tokens=*" %%j in ('wmic process where "processid=!PID!" get commandline /value ^| find "personal-blog-system"') do (
        echo [INFO] 找到博客应用进程: !PID!
        echo [INFO] 正在停止进程...
        taskkill /pid !PID! /f >nul 2>&1
        if !errorlevel! equ 0 (
            echo [INFO] 进程已停止
        ) else (
            echo [ERROR] 无法停止进程
        )
        goto :cleanup
    )
)

echo [INFO] 未找到运行中的博客应用

:cleanup
echo [INFO] 清理完成
pause