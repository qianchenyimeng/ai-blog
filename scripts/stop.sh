#!/bin/bash

# 个人博客系统停止脚本

set -e

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
PID_FILE="$PROJECT_DIR/blog.pid"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 停止应用
stop_application() {
    if [ ! -f "$PID_FILE" ]; then
        log_warn "PID文件不存在: $PID_FILE"
        
        # 尝试通过进程名查找
        local pids=$(pgrep -f "personal-blog-system.*\.jar")
        if [ -n "$pids" ]; then
            log_info "找到运行中的博客应用进程: $pids"
            for pid in $pids; do
                stop_process $pid
            done
        else
            log_info "未找到运行中的博客应用"
        fi
        return
    fi
    
    local pid=$(cat "$PID_FILE")
    
    if [ -z "$pid" ]; then
        log_error "PID文件为空"
        rm -f "$PID_FILE"
        return
    fi
    
    stop_process $pid
    rm -f "$PID_FILE"
}

# 停止指定进程
stop_process() {
    local pid=$1
    
    if ! kill -0 $pid 2>/dev/null; then
        log_warn "进程 $pid 不存在"
        return
    fi
    
    log_info "正在停止进程 $pid..."
    
    # 发送TERM信号
    kill -TERM $pid
    
    # 等待进程停止
    local count=0
    local max_wait=30
    
    while [ $count -lt $max_wait ]; do
        if ! kill -0 $pid 2>/dev/null; then
            log_info "进程 $pid 已停止"
            return
        fi
        
        sleep 1
        count=$((count + 1))
        
        if [ $((count % 5)) -eq 0 ]; then
            log_info "等待进程停止... ($count/$max_wait)"
        fi
    done
    
    # 强制停止
    log_warn "进程 $pid 未在规定时间内停止，强制终止..."
    kill -KILL $pid 2>/dev/null || true
    
    # 再次检查
    sleep 2
    if kill -0 $pid 2>/dev/null; then
        log_error "无法停止进程 $pid"
    else
        log_info "进程 $pid 已强制停止"
    fi
}

# 清理资源
cleanup() {
    log_info "清理临时文件..."
    
    # 清理PID文件
    rm -f "$PID_FILE"
    
    # 清理临时上传文件（可选）
    # rm -rf "$PROJECT_DIR/uploads/temp"
    
    log_info "清理完成"
}

# 显示状态
show_status() {
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if kill -0 $pid 2>/dev/null; then
            log_info "应用正在运行，PID: $pid"
            
            # 显示进程信息
            ps -p $pid -o pid,ppid,cmd,etime,pcpu,pmem
            
            # 显示端口信息
            local ports=$(lsof -Pan -p $pid -i | grep LISTEN | awk '{print $9}' | cut -d: -f2 | sort -u)
            if [ -n "$ports" ]; then
                log_info "监听端口: $ports"
            fi
        else
            log_warn "PID文件存在但进程不在运行"
            rm -f "$PID_FILE"
        fi
    else
        # 检查是否有相关进程在运行
        local pids=$(pgrep -f "personal-blog-system.*\.jar" || true)
        if [ -n "$pids" ]; then
            log_warn "发现运行中的博客应用进程（无PID文件）: $pids"
        else
            log_info "应用未运行"
        fi
    fi
}

# 主函数
main() {
    case "${1:-stop}" in
        stop)
            log_info "停止个人博客系统..."
            stop_application
            cleanup
            ;;
        status)
            show_status
            ;;
        restart)
            log_info "重启个人博客系统..."
            stop_application
            cleanup
            sleep 2
            
            # 调用启动脚本
            if [ -f "$SCRIPT_DIR/start.sh" ]; then
                "$SCRIPT_DIR/start.sh" "${@:2}"
            else
                log_error "启动脚本不存在: $SCRIPT_DIR/start.sh"
                exit 1
            fi
            ;;
        *)
            echo "使用方法: $0 {stop|status|restart}"
            echo "  stop    - 停止应用"
            echo "  status  - 显示应用状态"
            echo "  restart - 重启应用"
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"