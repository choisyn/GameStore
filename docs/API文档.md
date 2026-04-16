# 游戏社区商城系统 - API接口文档

**最后更新**: 2024-10-18  
**版本**: 1.0.0  
**数据库**: MySQL (已启用)

## 接口规范

### 基础信息
- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **认证方式**: 暂未启用（开发阶段）

### 统一响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2024-10-18T12:00:00"
}
```

### 状态码说明
- `200` - 成功
- `400` - 请求参数错误
- `401` - 未认证
- `403` - 无权限
- `404` - 资源不存在
- `500` - 服务器内部错误

---

## 游戏相关接口

### 1. 获取游戏列表
获取所有游戏或按条件筛选

**接口地址**
```
GET /api/games
```

**请求参数**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| page | Integer | 否 | 页码，从0开始 | 0 |
| size | Integer | 否 | 每页数量 | 12 |
| categoryId | Long | 否 | 分类ID | 2 |
| keyword | String | 否 | 搜索关键词 | 赛博朋克 |

**请求示例**
```
GET /api/games?page=0&size=12
GET /api/games?categoryId=2&page=0&size=10
GET /api/games?keyword=免费&page=0&size=10
```

**响应示例**
```json
{
  "code": 200,
  "message": "获取游戏列表成功",
  "data": [
    {
      "id": 1,
      "name": "赛博朋克2077",
      "description": "在反乌托邦的夜之城展开冒险...",
      "categoryId": 2,
      "developer": "CD Projekt RED",
      "publisher": "CD Projekt",
      "releaseDate": "2020-12-10",
      "price": 298.00,
      "discountPrice": null,
      "imageUrl": "https://images.unsplash.com/...",
      "systemRequirements": "最低配置：Windows 10 64-bit...",
      "tags": "RPG,开放世界,科幻,动作,单人",
      "rating": 4.2,
      "ratingCount": 15420,
      "downloadCount": 45000,
      "isFeatured": true,
      "status": "ACTIVE"
    }
  ]
}
```

---

### 2. 获取游戏详情
根据ID获取单个游戏的详细信息

**接口地址**
```
GET /api/games/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| id | Long | 是 | 游戏ID | 1 |

**请求示例**
```
GET /api/games/1
```

**响应示例**
```json
{
  "code": 200,
  "message": "获取游戏详情成功",
  "data": {
    "id": 1,
    "name": "赛博朋克2077",
    "description": "在反乌托邦的夜之城展开冒险，成为城市传奇。开放世界、动作冒险故事...",
    "categoryId": 2,
    "category": {
      "id": 2,
      "name": "RPG",
      "description": "角色扮演游戏"
    },
    "developer": "CD Projekt RED",
    "publisher": "CD Projekt",
    "releaseDate": "2020-12-10",
    "price": 298.00,
    "discountPrice": null,
    "imageUrl": "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&h=450&fit=crop",
    "gallery": null,
    "systemRequirements": "最低配置：Windows 10 64-bit | Intel Core i5-3570K | 8 GB RAM...\n\n推荐配置：Windows 10 64-bit | Intel Core i7-4790...",
    "tags": "RPG,开放世界,科幻,动作,单人",
    "rating": 4.2,
    "ratingCount": 15420,
    "downloadCount": 45000,
    "isFeatured": true,
    "status": "ACTIVE",
    "createdAt": "2024-10-18T10:00:00",
    "updatedAt": "2024-10-18T10:00:00"
  }
}
```

---

### 3. 获取精选游戏
获取标记为精选的游戏列表（用于首页轮播图）

**接口地址**
```
GET /api/games/featured
```

**请求参数**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| page | Integer | 否 | 页码，从0开始 | 0 |
| size | Integer | 否 | 每页数量 | 4 |

**请求示例**
```
GET /api/games/featured?page=0&size=4
```

**响应示例**
```json
{
  "code": 200,
  "message": "获取推荐游戏成功",
  "data": [
    {
      "id": 1,
      "name": "赛博朋克2077",
      "price": 298.00,
      "imageUrl": "https://images.unsplash.com/...",
      "isFeatured": true
    },
    {
      "id": 2,
      "name": "巫师3：狂猎",
      "price": 127.00,
      "discountPrice": 89.00,
      "imageUrl": "https://images.unsplash.com/...",
      "isFeatured": true
    }
  ]
}
```

---

### 4. 搜索游戏
根据关键词搜索游戏（搜索名称和描述）

**接口地址**
```
GET /api/games/search
```

**请求参数**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| keyword | String | 是 | 搜索关键词 | 免费 |
| page | Integer | 否 | 页码，从0开始 | 0 |
| size | Integer | 否 | 每页数量 | 10 |

**请求示例**
```
GET /api/games/search?keyword=免费&page=0&size=10
```

**响应示例**
```json
{
  "code": 200,
  "message": "搜索游戏成功",
  "data": [
    {
      "id": 7,
      "name": "Apex英雄",
      "description": "免费大逃杀射击游戏...",
      "price": 0.00,
      "tags": "免费,大逃杀,FPS,多人,竞技"
    },
    {
      "id": 11,
      "name": "英雄联盟",
      "description": "全球最受欢迎的MOBA游戏...",
      "price": 0.00,
      "tags": "免费,MOBA,竞技,多人,策略"
    }
  ]
}
```

---

## 分类相关接口

### 1. 获取所有分类
获取所有活跃状态的分类

**接口地址**
```
GET /api/categories
```

**请求示例**
```
GET /api/categories
```

**响应示例**
```json
{
  "code": 200,
  "message": "获取分类列表成功",
  "data": [
    {
      "id": 1,
      "name": "动作游戏",
      "description": "快节奏的动作游戏",
      "parentId": null,
      "sortOrder": 1,
      "iconUrl": null,
      "status": "ACTIVE"
    },
    {
      "id": 2,
      "name": "RPG游戏",
      "description": "角色扮演游戏",
      "parentId": null,
      "sortOrder": 2,
      "iconUrl": null,
      "status": "ACTIVE"
    }
  ]
}
```

---

### 2. 获取根分类
获取所有顶级分类（没有父分类）

**接口地址**
```
GET /api/categories/root
```

**请求示例**
```
GET /api/categories/root
```

**响应示例**
```json
{
  "code": 200,
  "message": "获取根分类成功",
  "data": [
    {
      "id": 1,
      "name": "动作游戏",
      "parentId": null,
      "sortOrder": 1
    },
    {
      "id": 2,
      "name": "RPG游戏",
      "parentId": null,
      "sortOrder": 2
    }
  ]
}
```

---

### 3. 获取分类详情
根据ID获取单个分类的详细信息

**接口地址**
```
GET /api/categories/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| id | Long | 是 | 分类ID | 2 |

**请求示例**
```
GET /api/categories/2
```

**响应示例**
```json
{
  "code": 200,
  "message": "获取分类详情成功",
  "data": {
    "id": 2,
    "name": "RPG游戏",
    "description": "角色扮演游戏",
    "parentId": null,
    "sortOrder": 2,
    "iconUrl": null,
    "status": "ACTIVE",
    "createdAt": "2024-10-18T10:00:00",
    "updatedAt": "2024-10-18T10:00:00"
  }
}
```

---

### 4. 获取子分类
获取指定分类的所有子分类

**接口地址**
```
GET /api/categories/{id}/children
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| id | Long | 是 | 父分类ID | 1 |

**请求示例**
```
GET /api/categories/1/children
```

**响应示例**
```json
{
  "code": 200,
  "message": "获取子分类成功",
  "data": [
    {
      "id": 11,
      "name": "动作冒险",
      "parentId": 1,
      "sortOrder": 1
    },
    {
      "id": 12,
      "name": "格斗游戏",
      "parentId": 1,
      "sortOrder": 2
    }
  ]
}
```

---

## 测试接口

### 1. 简单Ping测试
测试服务是否正常运行

**接口地址**
```
GET /api/simple/ping
```

**响应示例**
```json
{
  "code": 200,
  "message": "pong",
  "data": "Server is running!"
}
```

---

### 2. 系统信息
获取系统基本信息

**接口地址**
```
GET /api/simple/info
```

**响应示例**
```json
{
  "code": 200,
  "message": "System information",
  "data": {
    "name": "GameCommunityStore",
    "version": "1.0.0",
    "timestamp": "2024-10-18T12:00:00"
  }
}
```

---

### 3. 健康检查
检查应用健康状态

**接口地址**
```
GET /api/health
```

**响应示例**
```json
{
  "status": "UP"
}
```

---

### 4. Hello测试
简单的Hello World测试

**接口地址**
```
GET /api/test/hello
```

**响应示例**
```json
{
  "code": 200,
  "message": "Hello from GameStore!",
  "data": {
    "message": "Welcome to Game Community Store API"
  }
}
```

---

### 5. 内存数据测试
获取内存中的测试数据

**接口地址**
```
GET /api/test/memory
```

**响应示例**
```json
{
  "code": 200,
  "message": "Memory data loaded successfully",
  "data": {
    "userCount": 2,
    "categoryCount": 5,
    "gameCount": 12
  }
}
```

---

## 页面路由

### 1. 首页
访问网站首页，展示游戏商城

**路由地址**
```
GET /
```

**页面内容**
- 顶部导航栏
- 轮播大图（4款精选游戏）
- 分类筛选标签
- 游戏网格展示（12款游戏）
- 搜索功能
- 加载更多按钮

---

### 2. 游戏详情页
查看单个游戏的完整信息

**路由地址**
```
GET /game/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| id | Long | 是 | 游戏ID | 1 |

**访问示例**
```
GET /game/1   -> 赛博朋克2077
GET /game/2   -> 巫师3：狂猎
GET /game/12  -> DOTA 2
```

**页面内容**
- 游戏名称
- 游戏封面大图（500px）
- 游戏简介和标签
- 游戏信息（开发商、发行商、发售日期、下载数）
- 系统需求详情
- 评分展示（星星+数字+评价数）
- 价格信息（原价/折扣/免费）
- 购买按钮
- 愿望单按钮

---

## 数据模型

### Game（游戏）
```json
{
  "id": "Long - 游戏ID",
  "name": "String - 游戏名称",
  "description": "String - 游戏描述",
  "categoryId": "Long - 分类ID",
  "category": "Category - 分类对象（可为空）",
  "developer": "String - 开发商",
  "publisher": "String - 发行商",
  "releaseDate": "LocalDate - 发售日期",
  "price": "BigDecimal - 原价",
  "discountPrice": "BigDecimal - 折扣价（可为空）",
  "imageUrl": "String - 封面图URL",
  "gallery": "String - 图片集（可为空）",
  "systemRequirements": "String - 系统需求（文本格式）",
  "tags": "String - 标签（逗号分隔）",
  "rating": "BigDecimal - 评分（0-5）",
  "ratingCount": "Integer - 评价数量",
  "downloadCount": "Integer - 下载次数",
  "isFeatured": "Boolean - 是否精选",
  "status": "GameStatus - 游戏状态",
  "createdAt": "LocalDateTime - 创建时间",
  "updatedAt": "LocalDateTime - 更新时间"
}
```

**GameStatus枚举**
- `ACTIVE` - 活跃状态
- `INACTIVE` - 下架状态
- `COMING_SOON` - 即将推出

---

### Category（分类）
```json
{
  "id": "Long - 分类ID",
  "name": "String - 分类名称",
  "description": "String - 分类描述",
  "parentId": "Long - 父分类ID（可为空）",
  "sortOrder": "Integer - 排序顺序",
  "iconUrl": "String - 图标URL（可为空）",
  "status": "CategoryStatus - 分类状态",
  "createdAt": "LocalDateTime - 创建时间",
  "updatedAt": "LocalDateTime - 更新时间"
}
```

**CategoryStatus枚举**
- `ACTIVE` - 活跃状态
- `INACTIVE` - 停用状态

---

### User（用户）
```json
{
  "id": "Long - 用户ID",
  "username": "String - 用户名",
  "email": "String - 邮箱",
  "password": "String - 密码（加密）",
  "avatar": "String - 头像URL",
  "role": "UserRole - 用户角色",
  "status": "UserStatus - 用户状态",
  "createdAt": "LocalDateTime - 创建时间",
  "updatedAt": "LocalDateTime - 更新时间",
  "lastLoginAt": "LocalDateTime - 最后登录时间"
}
```

**UserRole枚举**
- `USER` - 普通用户
- `ADMIN` - 管理员
- `MODERATOR` - 版主

**UserStatus枚举**
- `ACTIVE` - 活跃状态
- `INACTIVE` - 未激活
- `BANNED` - 已封禁

---

## 错误码说明

### 业务错误码
| 错误码 | 说明 | 示例 |
|--------|------|------|
| 1001 | 用户不存在 | 找不到指定用户 |
| 1002 | 密码错误 | 登录密码不正确 |
| 1003 | 用户名已存在 | 注册时用户名重复 |
| 2001 | 游戏不存在 | 找不到指定游戏 |
| 2002 | 游戏已下架 | 游戏状态为INACTIVE |
| 3001 | 分类不存在 | 找不到指定分类 |

### 错误响应格式
```json
{
  "code": 400,
  "message": "游戏不存在",
  "data": null,
  "timestamp": "2024-10-18T12:00:00"
}
```

---

## 使用示例

### JavaScript (Fetch API)
```javascript
// 获取游戏列表
fetch('http://localhost:8080/api/games?page=0&size=12')
  .then(response => response.json())
  .then(data => {
    console.log('Games:', data.data);
  });

// 获取游戏详情
fetch('http://localhost:8080/api/games/1')
  .then(response => response.json())
  .then(data => {
    console.log('Game Detail:', data.data);
  });

// 搜索游戏
fetch('http://localhost:8080/api/games/search?keyword=免费')
  .then(response => response.json())
  .then(data => {
    console.log('Search Results:', data.data);
  });
```

### cURL
```bash
# 获取游戏列表
curl http://localhost:8080/api/games?page=0&size=12

# 获取游戏详情
curl http://localhost:8080/api/games/1

# 搜索游戏
curl "http://localhost:8080/api/games/search?keyword=免费&page=0&size=10"

# 获取分类
curl http://localhost:8080/api/categories
```

---

## 讨论广场接口 ✅已实现

### 1. 获取帖子列表
获取所有帖子或按游戏筛选

**接口地址**
```
GET /api/posts
```

**请求参数**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| page | Integer | 否 | 页码，从0开始 | 0 |
| size | Integer | 否 | 每页数量 | 20 |
| gameId | Long | 否 | 游戏ID，用于筛选 | 1 |

**请求示例**
```
GET /api/posts?page=0&size=20
GET /api/posts?gameId=1&page=0&size=20
```

**响应示例**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "赛博朋克2077 游戏体验分享",
        "content": "经过几十小时的游戏体验,我想分享一下对这款游戏的看法...",
        "userId": 3,
        "username": "admin",
        "userAvatar": null,
        "gameId": 1,
        "gameName": "赛博朋克2077",
        "category": "游戏评测",
        "viewCount": 0,
        "likeCount": 0,
        "commentCount": 0,
        "isPinned": false,
        "isFeatured": false,
        "status": "PUBLISHED",
        "createdAt": "2025-10-18T21:24:03",
        "updatedAt": "2025-10-18T21:24:03",
        "lastCommentAt": "2025-10-18T21:24:03"
      }
    ],
    "pageable": {...},
    "totalElements": 4,
    "totalPages": 1,
    "size": 20,
    "number": 0
  },
  "timestamp": "2025-10-18T21:26:26"
}
```

---

### 2. 获取帖子详情
根据ID获取单个帖子的详细信息

**接口地址**
```
GET /api/posts/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| id | Long | 是 | 帖子ID | 1 |

**请求示例**
```
GET /api/posts/1
```

**响应示例**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "title": "赛博朋克2077 游戏体验分享",
    "content": "经过几十小时的游戏体验...",
    "userId": 3,
    "username": "admin",
    "gameId": 1,
    "gameName": "赛博朋克2077",
    "category": "游戏评测",
    "viewCount": 1,
    "commentCount": 0
  }
}
```

---

### 3. 创建帖子
创建新的讨论帖子（需要登录）

**接口地址**
```
POST /api/posts
```

**请求头**
```
Authorization: Bearer {token}
```
或使用Cookie: `SESSION_TOKEN={token}`

**请求体**
```json
{
  "title": "帖子标题",
  "content": "帖子内容",
  "gameId": 1,
  "category": "游戏评测"
}
```

**请求参数说明**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | String | 是 | 标题(最多200字符) |
| content | String | 是 | 内容 |
| gameId | Long | 否 | 关联游戏ID |
| category | String | 否 | 分类 |

**响应示例**
```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "id": 5,
    "title": "帖子标题",
    "content": "帖子内容",
    "userId": 3,
    "username": "admin",
    "gameId": 1,
    "gameName": "赛博朋克2077",
    "status": "PUBLISHED"
  }
}
```

---

### 4. 删除帖子
删除自己的帖子（需要登录）

**接口地址**
```
DELETE /api/posts/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 帖子ID |

**请求头**
```
Authorization: Bearer {token}
```

**响应示例**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 5. 获取评论列表
获取某个帖子的所有评论

**接口地址**
```
GET /api/posts/{postId}/comments
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| postId | Long | 是 | 帖子ID |

**请求参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| page | Integer | 否 | 页码 | 0 |
| size | Integer | 否 | 每页数量 | 10 |

**请求示例**
```
GET /api/posts/1/comments?page=0&size=10
```

**响应示例**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "postId": 1,
        "userId": 3,
        "username": "admin",
        "parentId": null,
        "content": "这是一条评论",
        "likeCount": 0,
        "status": "PUBLISHED",
        "createdAt": "2025-10-18T21:30:00",
        "replies": []
      }
    ],
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

### 6. 创建评论
在帖子下发表评论（需要登录）

**接口地址**
```
POST /api/comments
```

**请求头**
```
Authorization: Bearer {token}
```

**请求体**
```json
{
  "postId": 1,
  "content": "评论内容",
  "parentId": null
}
```

**请求参数说明**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| postId | Long | 是 | 帖子ID |
| content | String | 是 | 评论内容 |
| parentId | Long | 否 | 父评论ID(回复评论时使用) |

**响应示例**
```json
{
  "code": 200,
  "message": "评论成功",
  "data": {
    "id": 2,
    "postId": 1,
    "userId": 3,
    "username": "admin",
    "content": "评论内容",
    "status": "PUBLISHED"
  }
}
```

---

### 7. 删除评论
删除自己的评论（需要登录）

**接口地址**
```
DELETE /api/comments/{id}
```

**路径参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 评论ID |

**请求头**
```
Authorization: Bearer {token}
```

**响应示例**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 8. 搜索帖子
根据关键词搜索帖子

**接口地址**
```
GET /api/posts/search
```

**请求参数**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | String | 是 | 搜索关键词 |
| page | Integer | 否 | 页码 | 0 |
| size | Integer | 否 | 每页数量 | 20 |

**请求示例**
```
GET /api/posts/search?keyword=攻略&page=0&size=20
```

**响应示例**
```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "content": [...],
    "totalElements": 2,
    "totalPages": 1
  }
}
```

---

## 重要提示

### API响应格式统一说明
所有API接口返回格式统一为:
```json
{
  "code": 200,           // 状态码: 200成功, 400参数错误, 500服务器错误
  "message": "操作成功",  // 提示信息
  "data": {},            // 数据内容
  "timestamp": "2025-10-18T21:26:26"  // 时间戳
}
```

### 前端调用注意事项
**⚠️ 重要**: 前端JavaScript代码检查API响应时,应使用`result.code === 200`而不是`result.success`:

```javascript
// ❌ 错误写法
if (result.success && result.data) {
    // ...
}

// ✅ 正确写法
if (result.code === 200 && result.data) {
    // ...
}
```

### 认证方式
讨论广场使用**Session Token**认证,而非JWT:
- Token存储在Cookie中,字段名为`SESSION_TOKEN`
- 也可以在HTTP Header中传递: `Authorization: Bearer {token}`
- 登录后由`AuthService`生成并管理Session

---

## 开发计划

### 待实现接口

#### 用户相关
- [x] POST `/api/auth/register` - 用户注册 ✅
- [x] POST `/api/auth/login` - 用户登录 ✅
- [x] POST `/api/auth/logout` - 用户登出 ✅
- [ ] GET `/api/users/me` - 获取当前用户信息
- [ ] PUT `/api/users/me` - 更新用户信息

#### 讨论广场相关
- [x] GET `/api/posts` - 获取帖子列表 ✅
- [x] GET `/api/posts/{id}` - 获取帖子详情 ✅
- [x] POST `/api/posts` - 创建帖子 ✅
- [x] DELETE `/api/posts/{id}` - 删除帖子 ✅
- [x] GET `/api/posts/{postId}/comments` - 获取评论列表 ✅
- [x] POST `/api/comments` - 创建评论 ✅
- [x] DELETE `/api/comments/{id}` - 删除评论 ✅
- [x] GET `/api/posts/search` - 搜索帖子 ✅

#### 购物车相关
- [ ] POST `/api/cart/add` - 添加到购物车
- [ ] GET `/api/cart` - 获取购物车
- [ ] DELETE `/api/cart/{id}` - 移除购物车项
- [ ] PUT `/api/cart/{id}` - 更新购物车项

#### 订单相关
- [ ] POST `/api/orders` - 创建订单
- [ ] GET `/api/orders` - 获取订单列表
- [ ] GET `/api/orders/{id}` - 获取订单详情
- [ ] PUT `/api/orders/{id}/cancel` - 取消订单

#### 评价相关
- [ ] POST `/api/games/{id}/reviews` - 发表评价
- [ ] GET `/api/games/{id}/reviews` - 获取游戏评价
- [ ] PUT `/api/reviews/{id}` - 更新评价
- [ ] DELETE `/api/reviews/{id}` - 删除评价

---

**最后更新**: 2025-10-18 21:30  
**文档维护**: 请在每次API更新后同步更新本文档  
**联系方式**: 如有问题请联系开发团队