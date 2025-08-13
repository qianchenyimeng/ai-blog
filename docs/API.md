# API 文档

个人博客系统 RESTful API 接口文档

## 基础信息

- **Base URL**: `http://localhost:8080`
- **API Version**: v1.0
- **Content-Type**: `application/json`
- **Authentication**: Session-based

## 认证说明

本系统使用基于Session的认证机制。用户需要先登录获取会话，然后在后续请求中携带会话信息。

### 登录
```http
POST /login
Content-Type: application/x-www-form-urlencoded

username=your_username&password=your_password
```

### 注销
```http
POST /logout
```

## 用户管理 API

### 用户注册
注册新用户账号

**请求**
```http
POST /register
Content-Type: application/x-www-form-urlencoded

username=newuser&email=user@example.com&password=password123&confirmPassword=password123&displayName=New User
```

**响应**
```http
HTTP/1.1 302 Found
Location: /login
```

**错误响应**
```json
{
  "timestamp": "2023-12-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "用户名已存在",
  "path": "/register"
}
```

### 获取用户资料
获取当前登录用户的资料信息

**请求**
```http
GET /profile
Authorization: Required (Session)
```

**响应**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- 用户资料页面 -->
```

### 更新用户资料
更新当前登录用户的资料信息

**请求**
```http
POST /profile
Content-Type: application/x-www-form-urlencoded
Authorization: Required (Session)

username=updateduser&email=updated@example.com&displayName=Updated User
```

**响应**
```http
HTTP/1.1 302 Found
Location: /profile
```

### 修改密码
修改当前登录用户的密码

**请求**
```http
POST /change-password
Content-Type: application/x-www-form-urlencoded
Authorization: Required (Session)

currentPassword=oldpass&newPassword=newpass123&confirmNewPassword=newpass123
```

**响应**
```http
HTTP/1.1 302 Found
Location: /profile
```

### 检查用户名可用性
检查用户名是否已被使用

**请求**
```http
GET /check-username?username=testuser
```

**响应**
```json
true  // 可用
false // 不可用
```

### 检查邮箱可用性
检查邮箱是否已被使用

**请求**
```http
GET /check-email?email=test@example.com
```

**响应**
```json
true  // 可用
false // 不可用
```

## 博客管理 API

### 获取博客列表
获取博客文章列表（支持分页）

**请求**
```http
GET /?page=0&size=10
```

**查询参数**
- `page`: 页码（从0开始，默认0）
- `size`: 每页大小（默认10）

**响应**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- 博客列表页面 -->
```

### 获取博客详情
获取指定博客的详细信息

**请求**
```http
GET /blog/{id}
```

**路径参数**
- `id`: 博客ID

**响应**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- 博客详情页面 -->
```

**错误响应**
```http
HTTP/1.1 404 Not Found
```

### 创建博客
创建新的博客文章

**请求**
```http
POST /blog/create
Content-Type: application/x-www-form-urlencoded
Authorization: Required (Session)

title=My Blog Title&content=Blog content here&tags=Java,Spring&published=true
```

**表单参数**
- `title`: 博客标题（必填，1-200字符）
- `content`: 博客内容（必填）
- `summary`: 博客摘要（可选，最多500字符）
- `tags`: 标签（可选，逗号分隔）
- `published`: 是否发布（true/false）

**响应**
```http
HTTP/1.1 302 Found
Location: /blog/{id}
```

### 编辑博客
编辑现有博客文章

**请求**
```http
POST /blog/{id}/edit
Content-Type: application/x-www-form-urlencoded
Authorization: Required (Session)

title=Updated Title&content=Updated content&tags=Java,Spring Boot&published=true
```

**响应**
```http
HTTP/1.1 302 Found
Location: /blog/{id}
```

### 删除博客
删除指定博客文章

**请求**
```http
POST /blog/{id}/delete
Authorization: Required (Session)
```

**响应**
```http
HTTP/1.1 302 Found
Location: /my-blogs
```

### 获取我的博客
获取当前用户的博客列表

**请求**
```http
GET /my-blogs?page=0&size=10
Authorization: Required (Session)
```

**响应**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- 我的博客页面 -->
```

## 评论管理 API

### 添加评论
为指定博客添加评论

**请求**
```http
POST /blog/{blogId}/comment
Content-Type: application/x-www-form-urlencoded
Authorization: Required (Session)

content=This is my comment
```

**表单参数**
- `content`: 评论内容（必填，1-1000字符）

**响应**
```http
HTTP/1.1 302 Found
Location: /blog/{blogId}
```

### 删除评论
删除指定评论

**请求**
```http
DELETE /comment/{id}
Authorization: Required (Session)
```

**响应**
```http
HTTP/1.1 302 Found
Location: /blog/{blogId}
```

### 获取我的评论
获取当前用户的评论列表

**请求**
```http
GET /my-comments?page=0&size=10
Authorization: Required (Session)
```

**响应**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- 我的评论页面 -->
```

### 管理评论
获取当前用户博客的所有评论

**请求**
```http
GET /manage-comments?page=0&size=10
Authorization: Required (Session)
```

**响应**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- 评论管理页面 -->
```

## 搜索 API

### 搜索博客
根据关键词搜索博客

**请求**
```http
GET /search?keyword=java&page=0&size=10
```

**查询参数**
- `keyword`: 搜索关键词（必填）
- `page`: 页码（默认0）
- `size`: 每页大小（默认10）

**响应**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- 搜索结果页面 -->
```

## 标签 API

### 获取标签云
获取所有标签及其使用频率

**请求**
```http
GET /tags
```

**响应**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- 标签云页面 -->
```

### 按标签筛选
根据标签筛选博客

**请求**
```http
GET /tag/{tagName}?page=0&size=10
```

**路径参数**
- `tagName`: 标签名称

**响应**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- 标签筛选结果页面 -->
```

## 系统监控 API

### 健康检查
检查应用健康状态

**请求**
```http
GET /actuator/health
```

**响应**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 91943821312,
        "threshold": 10485760,
        "exists": true
      }
    }
  }
}
```

### 应用信息
获取应用基本信息

**请求**
```http
GET /actuator/info
```

**响应**
```json
{
  "app": {
    "name": "Personal Blog System",
    "description": "个人博客系统",
    "version": "1.0.0"
  },
  "build": {
    "time": "2023-12-01T10:00:00Z",
    "version": "1.0.0"
  }
}
```

### 性能指标
获取应用性能指标

**请求**
```http
GET /actuator/metrics
```

**响应**
```json
{
  "names": [
    "jvm.memory.used",
    "jvm.memory.max",
    "http.server.requests",
    "system.cpu.usage",
    "process.uptime"
  ]
}
```

## 错误码说明

### HTTP状态码
- `200 OK` - 请求成功
- `302 Found` - 重定向
- `400 Bad Request` - 请求参数错误
- `401 Unauthorized` - 未认证
- `403 Forbidden` - 权限不足
- `404 Not Found` - 资源不存在
- `500 Internal Server Error` - 服务器内部错误

### 业务错误码
- `USER_NOT_FOUND` - 用户不存在
- `BLOG_NOT_FOUND` - 博客不存在
- `UNAUTHORIZED` - 未授权访问
- `DUPLICATE_USERNAME` - 用户名重复
- `INVALID_PASSWORD` - 密码错误
- `VALIDATION_ERROR` - 数据验证失败

## 请求限制

### 频率限制
- 登录请求: 每分钟最多5次
- 注册请求: 每小时最多3次
- 评论请求: 每分钟最多10次
- 搜索请求: 每分钟最多30次

### 数据限制
- 博客标题: 最大200字符
- 博客内容: 最大50000字符
- 评论内容: 最大1000字符
- 用户名: 3-50字符
- 密码: 最少6字符

## 示例代码

### JavaScript (jQuery)
```javascript
// 检查用户名可用性
function checkUsername(username) {
    $.get('/check-username', { username: username })
        .done(function(available) {
            if (available) {
                console.log('用户名可用');
            } else {
                console.log('用户名已被使用');
            }
        });
}

// 添加评论
function addComment(blogId, content) {
    $.post('/blog/' + blogId + '/comment', {
        content: content,
        _token: $('meta[name="_token"]').attr('content')
    }).done(function() {
        location.reload();
    });
}
```

### cURL
```bash
# 用户注册
curl -X POST http://localhost:8080/register \
  -d "username=testuser&email=test@example.com&password=password123&confirmPassword=password123"

# 搜索博客
curl "http://localhost:8080/search?keyword=java&page=0&size=5"

# 健康检查
curl http://localhost:8080/actuator/health
```

## 更新日志

### v1.0.0 (2023-12-01)
- 初始版本发布
- 用户管理功能
- 博客管理功能
- 评论系统
- 搜索功能
- 标签系统