# A_项目功能实现框架技术栈说明

## 1. 文档说明

- 编写时间：2026-04-17
- 编写依据：当前仓库中的 `pom.xml`、`docker-compose.yml`、`src/main/java/com/gamestore/**`、`src/main/resources/**`、`src/test/java/**`、`开发文档/**`
- 说明范围：聚焦当前项目真实可运行的功能实现、框架结构、技术栈和数据库表分工；数据库表只做业务级说明，不展开完整字段和数据样本

## 2. 项目概况

本项目是一个面向毕业设计与本地演示场景的“游戏社区与数字商品商城系统”。项目采用单体 Web 架构，把前台商城、用户系统、讨论广场、社区板块、游戏攻略、积分成长、内容打赏和后台管理放在同一个 Spring Boot 工程中实现。

项目的实际运行形态不是前后端分离的 SPA，而是：

- 一部分页面由 Thymeleaf 模板渲染
- 一部分页面直接以静态 HTML 输出
- 页面内通过原生 JavaScript 调用 REST API 获取数据
- 后端通过 Spring MVC + Service + JPA Repository 完成业务处理

默认运行参数如下：

- 应用端口：`8080`
- 默认数据库：`bishe`
- 默认数据源：`jdbc:mysql://localhost:3307/bishe`
- 健康检查接口：`/api/health`

## 3. 整体架构

系统采用典型的单体分层架构：

```text
浏览器
  -> Thymeleaf 模板 / 静态 HTML / 原生 JavaScript
  -> Controller 接收页面请求与 REST 请求
  -> Service 处理业务规则
  -> Repository 访问 MySQL
  -> Entity/DTO 完成数据映射与响应封装
```

当前架构还有 3 个比较关键的支撑点：

1. `StaticResourceConfig` 将本地上传目录映射为 `/uploads/**`，支撑论坛帖子图片访问。
2. `LegacySchemaRepairRunner` 在启动时自动修补旧环境中的历史表结构，降低旧 SQL 与新实体不一致的风险。
3. `CurrentUserService` + `AuthService` 基于自定义 Session Token 完成登录态识别，而不是完整启用 Spring Security。

## 4. 框架与技术栈

| 层面 | 技术/组件 | 当前用途 |
| --- | --- | --- |
| 语言与运行时 | Java 21 | 后端主语言与运行环境 |
| 核心框架 | Spring Boot 3.2.0 | 应用启动、IOC、MVC、配置管理 |
| Web 层 | Spring Web | Controller、页面路由、REST API |
| 持久层 | Spring Data JPA | Repository 查询、实体映射 |
| ORM/连接池 | Hibernate + HikariCP | JPA 实体持久化与数据库连接池 |
| 模板引擎 | Thymeleaf | 首页、详情、个人中心、订单、攻略、后台等页面模板 |
| 前端基础 | HTML + CSS + 原生 JavaScript | 页面交互、接口调用、数据渲染 |
| UI 框架 | Bootstrap 5 + Bootstrap Icons | 页面布局、组件、图标 |
| 数据库 | MySQL 8 | 主业务数据存储 |
| 密码处理 | `PBKDF2WithHmacSHA256` | 用户密码加密与旧密码升级 |
| 文件上传 | Spring Multipart + 本地文件系统 | 论坛帖子图片上传 |
| 构建工具 | Maven / Maven Wrapper | 项目构建、测试、打包 |
| 容器化 | Dockerfile + Docker Compose | 本地部署、容器运行 |
| 测试 | Spring Boot Test、Mockito | 基础启动测试、`GameService` 逻辑测试 |

当前代码里还保留了 Redis、JWT、Spring Security Test 等配置痕迹，但主线业务并不依赖 Redis 会话、JWT 鉴权或完整的 Spring Security 登录链路。系统真实使用的是自定义 Session Token 方案。

## 5. 功能实现梳理

| 功能模块 | 前端入口 | 核心后端代码 | 功能实现说明 | 主要数据表 |
| --- | --- | --- | --- | --- |
| 首页商城与游戏目录 | `templates/index.html` | `HomeController`、`GameController`、`CategoryController`、`BannerController`、`GameService`、`CategoryService`、`BannerService` | 展示首页导航、轮播图、游戏列表、精选位、分类筛选和关键词搜索；首页既负责商城入口，也承载推荐区、社区板块入口等聚合内容 | `games`、`categories`、`game_categories`、`banners` |
| 游戏详情与购前决策 | `templates/game-detail.html` | `GameDetailController`、`GameController`、`StoreController`、`InnovationController` | 展示单个游戏的图文详情、价格、标签、系统需求、是否已拥有、加购/领取按钮，并提供购前决策分析 | `games`、`user_games`、`cart_items`、`user_behavior_logs` |
| 用户注册、登录与会话 | `templates/login.html`、`templates/register.html` | `AuthController`、`AuthService`、`CurrentUserService`、`PasswordService`、`UserService` | 完成注册、登录、退出登录、获取当前用户；Session Token 同时支持 `Authorization: Bearer` 与 `SESSION_TOKEN` Cookie | `users`、`user_sessions` |
| 个人中心 | `templates/profile.html` | `ProfileController`、`UserService`、`StoreService`、`InnovationController` | 展示账号信息、发帖/评论统计、订单数、游戏库数、积分流水、成长面板，并支持修改资料与密码、执行每日签到 | `users`、`point_transactions`、`user_behavior_logs`、`user_badges`、`posts`、`comments`、`community_posts`、`community_comments` |
| 购物车、结算、订单、游戏库 | `templates/cart.html`、`templates/orders.html`、`templates/library.html` | `StoreController`、`StoreService` | 完成加入购物车、勾选结算、订单生成、免费游戏领取、订单查询、已购游戏库展示 | `cart_items`、`orders`、`order_items`、`user_games` |
| 积分体系与积分商城 | `templates/points-shop.html` | `StoreController`、`StoreService`、`ProfileController` | 完成积分累计、积分流水查询、积分抵扣、积分兑换折扣卡、结算时消费卡使用 | `point_transactions`、`user_discount_cards`、`orders`、`users` |
| 讨论广场 | `static/forum.html`、`static/create-post.html`、`static/post-detail.html` | `PostController`、`PostService`、`PostImageStorageService` | 完成帖子列表、帖子详情、发帖、评论、删除、自定义搜索、图片上传和帖子内容图片解析渲染 | `posts`、`comments` |
| 社区板块 | `templates/community/index.html` | `CommunityPageController`、`CommunityController`、`CommunityService` | 提供社区首页、板块列表、板块帖子、精华贴、社区评论与用户社区内容查询；部分旧板块被服务层过滤掉，不再作为主展示内容 | `community_sections`、`community_posts`、`community_comments` |
| 游戏攻略 | `templates/guides/index.html`、`templates/guides/create.html`、`templates/guides/detail.html` | `GuidePageController`、`GameGuideController`、`GameGuideService` | 提供攻略列表、精选攻略、攻略详情、发布攻略、按游戏/难度/关键词筛选 | `game_guides`、`games`、`users` |
| 内容打赏 | `static/post-detail.html`、`templates/guides/detail.html` | `ContentRewardController`、`ContentRewardService` | 用户可对讨论广场帖子和游戏攻略进行积分打赏；系统同时维护打赏汇总、支持者榜单和积分收支 | `content_rewards`、`point_transactions`、`posts`、`game_guides`、`users` |
| 创新推荐、成长与行为日志 | `templates/index.html`、`templates/game-detail.html`、`templates/profile.html` | `InnovationController`、`InnovationService` | 实现推荐列表、购前决策分析、成长面板、签到、行为上报、兴趣关键词提取、勋章评估和缓存化推荐 | `user_behavior_logs`、`user_badges`、`user_games`、`cart_items`、`orders`、`posts`、`comments`、`community_posts`、`community_comments` |
| 后台管理 | `templates/admin/index.html`、`templates/admin/banners.html`、`static/js/admin/*.js` | `AdminDashboardController`、`AdminGameController`、`AdminUserController`、`AdminCommunityController`、`BannerController` | 提供后台仪表盘、游戏管理、用户管理、社区内容管理、轮播图管理；前端采用单个后台页面 + JS 动态切换模块的方式组织 | `users`、`games`、`posts`、`comments`、`community_posts`、`community_comments`、`orders`、`banners` |
| 运维与兼容修复 | 无独立页面 | `HealthController`、`StaticResourceConfig`、`LegacySchemaRepairRunner` | 提供健康检查、上传资源映射、旧表兼容修复和运行时配置支持 | `cart_items`、`orders`、`order_items`、`user_games`、`point_transactions`、`user_discount_cards` |

## 6. 数据库表按业务模块梳理

### 6.1 用户与账号

- `users`：用户主表，保存用户名、邮箱、密码、角色、状态、积分等基础信息
- `user_sessions`：登录会话表，保存 Session Token、IP、User-Agent、过期时间
- `user_badges`：成长勋章表，保存用户达成的成长徽章
- `user_behavior_logs`：用户行为日志表，保存浏览、加购、购买、签到、评论等事件

### 6.2 游戏目录与运营内容

- `games`：游戏主表，保存游戏信息、价格、封面、标签、评分等
- `categories`：分类表，支持父子分类
- `game_categories`：游戏与分类多对多关联表
- `banners`：首页/社区等位置的轮播图表
- `game_guides`：游戏攻略表，保存攻略正文、摘要、难度、时长、浏览量等

### 6.3 商城交易闭环

- `cart_items`：购物车表
- `orders`：订单表
- `order_items`：订单项表，固化购买时的商品快照
- `user_games`：用户游戏库表，记录已拥有的数字商品
- `point_transactions`：积分流水表
- `user_discount_cards`：用户持有的积分兑换折扣卡表

### 6.4 讨论与社区内容

- `posts`：讨论广场帖子表
- `comments`：讨论广场评论表
- `community_sections`：社区板块表
- `community_posts`：社区板块帖子表
- `community_comments`：社区板块评论表
- `content_rewards`：内容打赏记录表，打通帖子/攻略打赏能力

### 6.5 代码未作为当前主线依赖的历史表

根据现有开发文档和代码使用情况，`products`、`admin_logs`、`community_likes`、`community_favorites` 等历史表不属于当前主线功能依赖对象，更多是早期设计或预留痕迹，论文和答辩时不建议将其描述为“当前系统已投入使用的核心模块”。

## 7. 关键实现特点

- 认证方式是自定义 Session Token，不是完整 Spring Security 登录链路。
- 密码使用 PBKDF2 加密，并支持旧明文密码在登录时自动升级为密文。
- 商城交易闭环完整：浏览游戏 -> 加购 -> 结算 -> 生成订单 -> 写入用户游戏库 -> 发放积分。
- 推荐系统不是黑盒模型，而是基于购买、加购、浏览、发帖、评论等行为构建可解释规则推荐。
- 购前决策分析会综合标签匹配、社区热度、价格与折扣、资料完整度输出解释性结果。
- 个人中心的成长系统由签到、活跃任务、兴趣关键词、等级和勋章构成。
- 讨论广场支持图片上传，图片被保存到本地目录，并通过 `/uploads/**` 暴露访问。
- 项目前端是混合组织方式：商城/攻略/个人中心/后台主要用 Thymeleaf，讨论广场则保留静态 HTML 页面。
- 系统通过 `LegacySchemaRepairRunner` 在启动时兼容旧库结构，体现了从早期脚本向当前结构迁移的演进过程。

## 8. 总结

从当前代码来看，这个项目已经不是单纯的“首页 + 登录 + 游戏详情”演示，而是具备了完整业务闭环的单体系统：前台商城负责游戏浏览与交易，社区与攻略负责内容沉淀，推荐/成长/签到/打赏负责创新展示，后台管理负责运营控制，数据库则围绕用户、商品、交易、内容和行为 5 条主线组织。

如果后续继续扩展，最适合继续深挖的方向是：订单管理细化、后台权限拦截器统一化、内容审核流程、日志审计，以及把目前分散在模板和静态页中的前端交互进一步收敛。
