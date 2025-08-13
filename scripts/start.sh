#!/bin/bash

# 个人博客系统启动脚本
# 使用方法: ./start.sh [环境] [选项]
# 环境: dev, test, prod (默认: dev)
# 选项: --debug (启用调试模式)

set -e

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# 默认配置
PROFILE="dev"
DEBUG_MODE=false
JAR_FILE=""
JAVA_OPTS=""
LOG_DIR="$PROJECT_DIR/logs"

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        dev|test|prod)
            PROFILE="$1"
            shift
            ;;
        --debug)
            DEBUG_MODE=true
            shift
            ;;
        -h|--help)
            echo "使用方法: $0 [环境] [选项]"
            echo "环境:"
            echo "  dev     开发环境 (默认)"
            echo "  test    测试环境"
            echo "  prod    生产环境"
            echo "选项:"
            echo "  --debug 启用调试模式"
            echo "  -h, --help 显示帮助信息"
            exit 0
            ;;
        *)
            echo "未知参数: $1"
            echo "使用 -h 或 --help 查看帮助信息"
            exit 1
            ;;
    esac
done

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

log_debug() {
    if [ "$DEBUG_MODE" = true ]; then
        echo -e "${BLUE}[DEBUG]${NC} $1"
    fi
}

# 检查Java环境
check_java() {
    if ! command -v java &> /dev/null; then
        log_error "Java未安装或未在PATH中"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    log_info "Java版本: $JAVA_VERSION"
    
    # 检查Java版本是否为1.8或更高
    if [[ "$JAVA_VERSION" < "1.8" ]]; then
        log_error "需要Java 1.8或更高版本"
        exit 1
    fi
}

# 查找JAR文件
find_jar() {
    JAR_FILE=$(find "$PROJECT_DIR/target" -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -1)
    
    if [ -z "$JAR_FILE" ]; then
        log_error "未找到JAR文件，请先运行 mvn clean package"
        exit 1
    fi
    
    log_info "找到JAR文件: $JAR_FILE"
}

# 创建必要的目录
create_directories() {
    mkdir -p "$LOG_DIR"
    mkdir -p "$PROJECT_DIR/uploads"
    
    if [ "$PROFILE" = "prod" ]; then
        mkdir -p "/var/log/blog"
        mkdir -p "/var/blog/uploads"
    fi
}

# 设置JVM参数
setup_jvm_opts() {
    case $PROFILE in
        dev)
            JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC"
            ;;
        test)
            JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
            ;;
        prod)
            JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LOG_DIR"
            ;;
    esac
    
    # 调试模式
    if [ "$DEBUG_MODE" = true ]; then
        JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
        log_info "调试模式已启用，调试端口: 5005"
    fi
    
    # 添加JVM监控参数
    JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"
    JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"
    JAVA_OPTS="$JAVA_OPTS -Duser.timezone=Asia/Shanghai"
    
    log_debug "JVM参数: $JAVA_OPTS"
}

# 检查端口是否被占用
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        log_error "端口 $port 已被占用"
        exit 1
    fi
}

# 等待应用启动
wait_for_startup() {
    local port=$1
    local max_wait=60
    local count=0
    
    log_info "等待应用启动..."
    
    while [ $count -lt $max_wait ]; do
        if curl -s http://localhost:$port/actuator/health >/dev/null 2>&1; then
            log_info "应用启动成功！"
            return 0
        fi
        
        sleep 1
        count=$((count + 1))
        
        if [ $((count % 10)) -eq 0 ]; then
            log_info "等待中... ($count/$max_wait)"
        fi
    done
    
    log_error "应用启动超时"
    return 1
}

# 主函数
main() {
    log_info "启动个人博客系统..."
    log_info "环境: $PROFILE"
    
    # 检查环境
    check_java
    find_jar
    create_directories
    setup_jvm_opts
    
    # 检查端口
    local port=8080
    if [ "$PROFILE" = "test" ]; then
        port=0  # 随机端口
    fi
    
    if [ "$port" != "0" ]; then
        check_port $port
    fi
    
    # 设置环境变量
    export SPRING_PROFILES_ACTIVE=$PROFILE
    
    # 启动应用
    log_info "启动命令: java $JAVA_OPTS -jar $JAR_FILE --spring.profiles.active=$PROFILE"
    
    if [ "$PROFILE" = "prod" ]; then
        # 生产环境后台运行
        nohup java $JAVA_OPTS -jar "$JAR_FILE" --spring.profiles.active=$PROFILE > "$LOG_DIR/startup.log" 2>&1 &
        local pid=$!
        echo $pid > "$PROJECT_DIR/blog.pid"
        log_info "应用已在后台启动，PID: $pid"
        
        # 等待启动完成
        if wait_for_startup $port; then
            log_info "应用访问地址: http://localhost:$port"
            log_info "健康检查: http://localhost:$port/actuator/health"
            log_info "日志文件: $LOG_DIR/blog-app.log"
        fi
    else
        # 开发/测试环境前台运行
        java $JAVA_OPTS -jar "$JAR_FILE" --spring.profiles.active=$PROFILE
    fi
}

# 信号处理
trap 'log_info "收到中断信号，正在停止..."; exit 0' INT TERM

# 执行主函数
main "$@"