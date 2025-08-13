#!/bin/bash

# 个人博客系统部署脚本
# 支持本地部署和远程部署

set -e

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# 默认配置
ENVIRONMENT="prod"
REMOTE_HOST=""
REMOTE_USER=""
REMOTE_PATH="/opt/blog"
BACKUP_ENABLED=true
SKIP_TESTS=false
SKIP_BUILD=false

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
    echo -e "${BLUE}[DEBUG]${NC} $1"
}

# 显示帮助信息
show_help() {
    cat << EOF
个人博客系统部署脚本

使用方法: $0 [选项]

选项:
  -e, --env ENV           部署环境 (dev|test|prod，默认: prod)
  -h, --host HOST         远程主机地址
  -u, --user USER         远程用户名
  -p, --path PATH         远程部署路径 (默认: /opt/blog)
  --no-backup            跳过备份
  --skip-tests           跳过测试
  --skip-build           跳过构建
  --help                 显示帮助信息

示例:
  $0                                    # 本地部署
  $0 -h server.com -u deploy           # 远程部署
  $0 -e test --skip-tests               # 测试环境部署，跳过测试

EOF
}

# 解析命令行参数
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -e|--env)
                ENVIRONMENT="$2"
                shift 2
                ;;
            -h|--host)
                REMOTE_HOST="$2"
                shift 2
                ;;
            -u|--user)
                REMOTE_USER="$2"
                shift 2
                ;;
            -p|--path)
                REMOTE_PATH="$2"
                shift 2
                ;;
            --no-backup)
                BACKUP_ENABLED=false
                shift
                ;;
            --skip-tests)
                SKIP_TESTS=true
                shift
                ;;
            --skip-build)
                SKIP_BUILD=true
                shift
                ;;
            --help)
                show_help
                exit 0
                ;;
            *)
                log_error "未知参数: $1"
                show_help
                exit 1
                ;;
        esac
    done
}

# 检查环境
check_environment() {
    log_info "检查部署环境..."
    
    # 检查Java
    if ! command -v java &> /dev/null; then
        log_error "Java未安装"
        exit 1
    fi
    
    # 检查Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven未安装"
        exit 1
    fi
    
    # 检查Git
    if ! command -v git &> /dev/null; then
        log_warn "Git未安装，无法获取版本信息"
    fi
    
    log_info "环境检查完成"
}

# 构建应用
build_application() {
    if [ "$SKIP_BUILD" = true ]; then
        log_info "跳过构建步骤"
        return
    fi
    
    log_info "构建应用..."
    
    cd "$PROJECT_DIR"
    
    # 清理
    mvn clean
    
    # 构建参数
    local mvn_args="package -DskipTests=$SKIP_TESTS"
    
    if [ "$ENVIRONMENT" = "prod" ]; then
        mvn_args="$mvn_args -Pprod"
    fi
    
    # 执行构建
    log_info "执行: mvn $mvn_args"
    mvn $mvn_args
    
    # 检查构建结果
    local jar_file=$(find target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -1)
    if [ -z "$jar_file" ]; then
        log_error "构建失败，未找到JAR文件"
        exit 1
    fi
    
    log_info "构建完成: $jar_file"
}

# 创建部署包
create_deployment_package() {
    log_info "创建部署包..."
    
    local deploy_dir="$PROJECT_DIR/deploy"
    local timestamp=$(date +%Y%m%d_%H%M%S)
    local package_name="blog-${ENVIRONMENT}-${timestamp}.tar.gz"
    
    # 清理并创建部署目录
    rm -rf "$deploy_dir"
    mkdir -p "$deploy_dir"
    
    # 复制文件
    cp target/*.jar "$deploy_dir/"
    cp -r scripts "$deploy_dir/"
    cp -r src/main/resources/application*.yml "$deploy_dir/" 2>/dev/null || true
    
    # 创建配置目录
    mkdir -p "$deploy_dir/config"
    mkdir -p "$deploy_dir/logs"
    mkdir -p "$deploy_dir/uploads"
    
    # 复制数据库脚本
    if [ -d "scripts/database" ]; then
        cp -r scripts/database "$deploy_dir/"
    fi
    
    # 创建版本信息
    cat > "$deploy_dir/VERSION" << EOF
Application: Personal Blog System
Version: 1.0.0
Environment: $ENVIRONMENT
Build Time: $(date)
Git Commit: $(git rev-parse HEAD 2>/dev/null || echo "unknown")
Git Branch: $(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "unknown")
EOF
    
    # 打包
    cd "$PROJECT_DIR"
    tar -czf "$package_name" -C deploy .
    
    log_info "部署包创建完成: $package_name"
    echo "$PROJECT_DIR/$package_name"
}

# 本地部署
deploy_local() {
    log_info "执行本地部署..."
    
    local package_file=$(create_deployment_package)
    local deploy_path="/opt/blog"
    
    # 创建部署目录
    sudo mkdir -p "$deploy_path"
    
    # 备份现有部署
    if [ "$BACKUP_ENABLED" = true ] && [ -d "$deploy_path/current" ]; then
        local backup_name="backup_$(date +%Y%m%d_%H%M%S)"
        log_info "备份现有部署到: $deploy_path/$backup_name"
        sudo mv "$deploy_path/current" "$deploy_path/$backup_name"
    fi
    
    # 解压新版本
    sudo mkdir -p "$deploy_path/current"
    sudo tar -xzf "$package_file" -C "$deploy_path/current"
    
    # 设置权限
    sudo chown -R $USER:$USER "$deploy_path/current"
    sudo chmod +x "$deploy_path/current/scripts"/*.sh
    
    # 创建符号链接
    sudo ln -sf "$deploy_path/current/scripts/start.sh" /usr/local/bin/blog-start
    sudo ln -sf "$deploy_path/current/scripts/stop.sh" /usr/local/bin/blog-stop
    
    log_info "本地部署完成"
    log_info "启动命令: blog-start $ENVIRONMENT"
    log_info "停止命令: blog-stop"
}

# 远程部署
deploy_remote() {
    log_info "执行远程部署到 $REMOTE_USER@$REMOTE_HOST:$REMOTE_PATH"
    
    local package_file=$(create_deployment_package)
    local package_name=$(basename "$package_file")
    
    # 上传部署包
    log_info "上传部署包..."
    scp "$package_file" "$REMOTE_USER@$REMOTE_HOST:/tmp/$package_name"
    
    # 远程部署脚本
    cat > /tmp/remote_deploy.sh << 'EOF'
#!/bin/bash
set -e

PACKAGE_FILE="$1"
DEPLOY_PATH="$2"
BACKUP_ENABLED="$3"
ENVIRONMENT="$4"

# 创建部署目录
mkdir -p "$DEPLOY_PATH"

# 备份现有部署
if [ "$BACKUP_ENABLED" = "true" ] && [ -d "$DEPLOY_PATH/current" ]; then
    backup_name="backup_$(date +%Y%m%d_%H%M%S)"
    echo "备份现有部署到: $DEPLOY_PATH/$backup_name"
    mv "$DEPLOY_PATH/current" "$DEPLOY_PATH/$backup_name"
    
    # 保留最近5个备份
    cd "$DEPLOY_PATH"
    ls -t backup_* 2>/dev/null | tail -n +6 | xargs rm -rf
fi

# 解压新版本
mkdir -p "$DEPLOY_PATH/current"
tar -xzf "$PACKAGE_FILE" -C "$DEPLOY_PATH/current"

# 设置权限
chmod +x "$DEPLOY_PATH/current/scripts"/*.sh

# 清理临时文件
rm -f "$PACKAGE_FILE"

echo "远程部署完成"
EOF
    
    # 上传并执行部署脚本
    scp /tmp/remote_deploy.sh "$REMOTE_USER@$REMOTE_HOST:/tmp/"
    ssh "$REMOTE_USER@$REMOTE_HOST" "bash /tmp/remote_deploy.sh /tmp/$package_name $REMOTE_PATH $BACKUP_ENABLED $ENVIRONMENT"
    
    # 清理临时文件
    ssh "$REMOTE_USER@$REMOTE_HOST" "rm -f /tmp/remote_deploy.sh"
    rm -f /tmp/remote_deploy.sh
    
    log_info "远程部署完成"
    log_info "启动命令: ssh $REMOTE_USER@$REMOTE_HOST '$REMOTE_PATH/current/scripts/start.sh $ENVIRONMENT'"
}

# 部署后验证
verify_deployment() {
    log_info "验证部署..."
    
    local host="localhost"
    local port="8080"
    
    if [ -n "$REMOTE_HOST" ]; then
        host="$REMOTE_HOST"
    fi
    
    # 等待应用启动
    local max_wait=60
    local count=0
    
    while [ $count -lt $max_wait ]; do
        if curl -s "http://$host:$port/actuator/health" >/dev/null 2>&1; then
            log_info "应用验证成功！"
            log_info "访问地址: http://$host:$port"
            return 0
        fi
        
        sleep 2
        count=$((count + 2))
        
        if [ $((count % 10)) -eq 0 ]; then
            log_info "等待应用启动... ($count/$max_wait)"
        fi
    done
    
    log_warn "应用验证超时，请手动检查"
    return 1
}

# 主函数
main() {
    log_info "开始部署个人博客系统..."
    log_info "环境: $ENVIRONMENT"
    
    parse_args "$@"
    check_environment
    build_application
    
    if [ -n "$REMOTE_HOST" ]; then
        deploy_remote
    else
        deploy_local
    fi
    
    log_info "部署完成！"
    
    # 清理部署包
    rm -rf "$PROJECT_DIR/deploy"
    rm -f "$PROJECT_DIR"/blog-*.tar.gz
}

# 执行主函数
main "$@"