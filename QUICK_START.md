# 🚀 快速启动指南

## 一键运行项目

这个项目已经配置为可以直接运行，无需安装MySQL数据库！

### 方法1：使用Maven命令行
```bash
# 在项目根目录执行
mvn spring-boot:run
```

### 方法2：使用IDEA
1. 打开项目根目录（包含pom.xml的目录）
2. 等待Maven依赖下载完成
3. 找到 `src/main/java/com/blog/BlogApplication.java`
4. 右键点击 → Run 'BlogApplication'

### 方法3：使用JAR包
```bash
# 先打包
mvn clean package -DskipTests

# 运行JAR包
java -jar target/personal-blog-system-1.0.0.jar
```

## 🌐 访问应用

启动成功后，打开浏览器访问：

- **主页**: http://localhost:8080
- **登录页**: http://localhost:8080/login
- **注册页**: http://localhost:8080/register
- **H2数据库控制台**: http://localhost:8080/h2-console

## 👤 测试账号

系统会自动创建以下测试账号：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 管理员 | 系统管理员账号 |
| demo_user | user123 | 普通用户 | 演示用户账号 |

## 🎯 功能演示

### 1. 用户功能
- 使用测试账号登录
- 查看和编辑个人资料
- 修改密码

### 2. 博客功能
- 浏览博客列表（首页）
- 查看博客详情
- 创建新博客（需要登录）
- 编辑和删除自己的博客

### 3. 评论功能
- 在博客详情页发表评论
- 查看我的评论
- 管理博客评论

### 4. 搜索功能
- 使用搜索框搜索博客
- 按标签筛选博客
- 查看标签云

### 5. 管理功能
- 查看我的博客列表
- 管理博客评论
- 系统健康检查

## 🗄️ 数据库说明

项目使用H2内存数据库，特点：
- **无需安装**：内嵌数据库，启动即可用
- **数据重置**：每次重启应用，数据会重新初始化
- **控制台访问**：可通过Web界面查看数据库

### H2控制台连接信息
- **JDBC URL**: `jdbc:h2:mem:blogdb`
- **用户名**: `sa`
- **密码**: （留空）

## 🛠️ 开发调试

### 查看日志
应用启动后会在控制台显示详细日志，包括：
- SQL语句执行日志
- 安全认证日志
- 应用调试日志

### 热重载
项目包含Spring Boot DevTools，修改代码后会自动重启。

### API测试
可以使用以下工具测试API：
- **健康检查**: http://localhost:8080/actuator/health
- **应用信息**: http://localhost:8080/actuator/info

## ❗ 常见问题

### 1. 端口被占用
如果8080端口被占用，可以修改 `application.yml` 中的端口：
```yaml
server:
  port: 8081  # 改为其他端口
```

### 2. Maven依赖下载慢
可以配置阿里云镜像加速：
```xml
<!-- 在Maven settings.xml中添加 -->
<mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/central</url>
</mirror>
```

### 3. IDEA无法识别项目
确保：
- 项目根目录包含pom.xml
- IDEA已安装Maven插件
- 等待Maven依赖下载完成

## 🎉 开始体验

现在你可以开始体验这个功能完整的个人博客系统了！

系统包含了现代Web应用的所有核心功能：
- ✅ 用户认证和授权
- ✅ 内容管理系统
- ✅ 评论互动系统
- ✅ 搜索和分类
- ✅ 响应式界面
- ✅ 安全防护
- ✅ 系统监控

祝你使用愉快！🚀