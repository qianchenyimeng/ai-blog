# 个人博客系统

一个基于Spring Boot开发的现代化个人博客系统，提供完整的博客管理、用户管理、评论系统等功能。

## 🚀 功能特性

### 用户管理
- ✅ 用户注册和登录
- ✅ 用户资料管理
- ✅ 密码修改
- ✅ 邮箱验证
- ✅ 用户权限控制

### 博客管理
- ✅ 博客文章创建、编辑、删除
- ✅ 富文本编辑器支持
- ✅ 博客分类和标签
- ✅ 博客发布状态控制
- ✅ 博客浏览量统计

### 评论系统
- ✅ 评论发表和管理
- ✅ 评论回复功能
- ✅ 评论审核机制
- ✅ 评论权限控制

### 搜索功能
- ✅ 全文搜索
- ✅ 标签筛选
- ✅ 高级搜索选项

### 系统功能
- ✅ 响应式设计
- ✅ 多主题支持
- ✅ 国际化支持
- ✅ SEO优化
- ✅ 安全防护（XSS、CSRF、SQL注入）

## 🛠 技术栈

### 后端技术
- **Spring Boot 2.7.18** - 主框架
- **Spring Security** - 安全框架
- **Spring Data JPA** - 数据访问层
- **MySQL 8.0** - 数据库
- **Maven** - 项目管理

### 前端技术
- **Thymeleaf** - 模板引擎
- **Bootstrap 5** - UI框架
- **jQuery** - JavaScript库
- **Font Awesome** - 图标库

### 开发工具
- **Spring Boot DevTools** - 开发工具
- **JUnit 5** - 单元测试
- **Mockito** - 模拟测试
- **H2 Database** - 测试数据库

## 📋 系统要求

### 运行环境
- **Java 8+** (推荐Java 11)
- **MySQL 8.0+**
- **Maven 3.6+**

### 硬件要求
- **内存**: 最小512MB，推荐1GB+
- **磁盘**: 最小100MB，推荐1GB+
- **CPU**: 1核心+

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/your-username/personal-blog-system.git
cd personal-blog-system
```

### 2. 配置数据库
```bash
# 创建数据库
mysql -u root -p < scripts/database/init.sql

# 或者手动创建
mysql -u root -p
CREATE DATABASE personal_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置应用
```bash
# 复制配置文件
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml

# 编辑配置文件，修改数据库连接信息
vim src/main/resources/application-dev.yml
```

### 4. 构建和运行
```bash
# 构建项目
mvn clean package

# 运行应用
./scripts/start.sh dev

# 或者直接运行
java -jar target/personal-blog-system-1.0.0.jar --spring.profiles.active=dev
```

### 5. 访问应用
- 应用地址: http://localhost:8080
- 管理员账号: admin / admin123
- 演示账号: demo_user / user123

## 📖 详细文档

### 配置说明

#### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/personal_blog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

#### 邮件配置（可选）
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password
```

#### 文件上传配置
```yaml
blog:
  upload:
    path: uploads
    max-file-size: 10MB
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx
```

### 环境配置

#### 开发环境
```bash
./scripts/start.sh dev
```

#### 测试环境
```bash
./scripts/start.sh test
```

#### 生产环境
```bash
./scripts/start.sh prod
```

### 部署指南

#### 本地部署
```bash
# 构建并部署到本地
./scripts/deploy.sh

# 启动服务
blog-start prod
```

#### 远程部署
```bash
# 部署到远程服务器
./scripts/deploy.sh -h your-server.com -u deploy -p /opt/blog

# 远程启动服务
ssh deploy@your-server.com '/opt/blog/current/scripts/start.sh prod'
```

### API文档

#### 健康检查
- `GET /actuator/health` - 应用健康状态
- `GET /actuator/info` - 应用信息

#### 用户API
- `POST /register` - 用户注册
- `POST /login` - 用户登录
- `GET /profile` - 用户资料
- `POST /profile` - 更新资料

#### 博客API
- `GET /` - 博客列表
- `GET /blog/{id}` - 博客详情
- `POST /blog/create` - 创建博客
- `POST /blog/{id}/edit` - 编辑博客

## 🧪 测试

### 运行测试
```bash
# 运行所有测试
mvn test

# 运行特定测试
mvn test -Dtest=UserServiceTest

# 生成测试报告
mvn test jacoco:report
```

### 测试覆盖率
- 单元测试覆盖率: 80%+
- 集成测试覆盖率: 70%+

## 📊 监控

### 应用监控
- **健康检查**: `/actuator/health`
- **应用信息**: `/actuator/info`
- **性能指标**: `/actuator/metrics`

### 日志监控
- **应用日志**: `logs/blog-app.log`
- **错误日志**: `logs/blog-error.log`
- **安全日志**: `logs/blog-security.log`

## 🔒 安全特性

### 安全防护
- **XSS防护** - 输入输出过滤
- **CSRF防护** - 跨站请求伪造防护
- **SQL注入防护** - 参数化查询
- **密码加密** - BCrypt加密
- **会话管理** - 安全会话配置

### 权限控制
- **角色权限** - 基于角色的访问控制
- **资源权限** - 细粒度权限控制
- **API权限** - RESTful API权限验证

## 🚀 性能优化

### 缓存策略
- **应用缓存** - Caffeine缓存
- **数据库缓存** - Hibernate二级缓存
- **静态资源缓存** - 浏览器缓存

### 数据库优化
- **连接池** - HikariCP连接池
- **索引优化** - 数据库索引优化
- **查询优化** - JPA查询优化

## 🐛 故障排除

### 常见问题

#### 1. 数据库连接失败
```
解决方案:
1. 检查数据库服务是否启动
2. 验证数据库连接配置
3. 确认数据库用户权限
```

#### 2. 端口被占用
```
解决方案:
1. 检查端口占用: lsof -i :8080
2. 停止占用进程: kill -9 <pid>
3. 或修改应用端口配置
```

#### 3. 内存不足
```
解决方案:
1. 增加JVM内存: -Xmx2g
2. 优化应用配置
3. 检查内存泄漏
```

### 日志分析
```bash
# 查看应用日志
tail -f logs/blog-app.log

# 查看错误日志
tail -f logs/blog-error.log

# 搜索特定错误
grep "ERROR" logs/blog-app.log
```

## 🤝 贡献指南

### 开发流程
1. Fork项目
2. 创建功能分支: `git checkout -b feature/new-feature`
3. 提交更改: `git commit -am 'Add new feature'`
4. 推送分支: `git push origin feature/new-feature`
5. 创建Pull Request

### 代码规范
- 遵循Java编码规范
- 添加适当的注释
- 编写单元测试
- 更新文档

## 📄 许可证

本项目采用 [MIT许可证](LICENSE)

## 📞 联系方式

- 项目主页: https://github.com/your-username/personal-blog-system
- 问题反馈: https://github.com/your-username/personal-blog-system/issues
- 邮箱: your-email@example.com

## 🙏 致谢

感谢以下开源项目的支持:
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Bootstrap](https://getbootstrap.com/)
- [jQuery](https://jquery.com/)
- [Font Awesome](https://fontawesome.com/)

---

⭐ 如果这个项目对你有帮助，请给它一个星标！