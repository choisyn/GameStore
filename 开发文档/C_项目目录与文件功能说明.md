# C_项目目录与文件功能说明

## 1. 说明

- 编写时间：2026-04-17
- 说明目标：梳理当前项目目录、源码文件、配置文件、脚本文件和文档文件各自承担的功能
- 说明范围：优先逐项说明项目自有源码、配置、SQL、开发文档和运行脚本
- 归并说明：`.git`、`.m2repo`、`.maven-home`、`.maven-user-home`、`target`、`src/main/resources/static/image`、`论文文档/完整的校方资料` 这类批量缓存/产物/素材目录不适合逐个做业务解释，因此按目录级说明

## 2. 根目录文件

| 路径 | 类型 | 作用 |
| --- | --- | --- |
| `.DS_Store` | 系统文件 | macOS 自动生成的目录元数据，对业务无影响 |
| `.dockerignore` | 配置文件 | Docker 构建时忽略不需要进入镜像的文件 |
| `.gitattributes` | 配置文件 | Git 文件属性配置 |
| `.gitignore` | 配置文件 | Git 忽略规则 |
| `.mvn-local-settings.xml` | 配置文件 | 本地 Maven 设置补充文件 |
| `Dockerfile` | 部署文件 | 定义项目镜像构建流程，镜像构建时会完成 Maven 打包 |
| `README.md` | 项目说明 | 项目简介、部署方式、运行地址和数据库初始化说明 |
| `docker-compose.yml` | 编排文件 | 定义应用容器与可选 MySQL 容器的启动方式 |
| `mvnw` | 构建脚本 | Unix/Linux/macOS 下的 Maven Wrapper 启动脚本 |
| `mvnw.cmd` | 构建脚本 | Windows 下的 Maven Wrapper 启动脚本 |
| `pom.xml` | 构建配置 | Maven 坐标、依赖、插件和 Java 版本配置 |
| `test-compile.bat` | 辅助脚本 | Windows 下的测试/编译辅助脚本 |
| `tmp_current_doc_extract.txt` | 临时文件 | 文档提取过程产生的临时文本，不属于业务代码 |
| `tmp_db_games.txt` | 临时文件 | 数据整理过程中的临时导出文本 |
| `tmp_names_12.txt` | 临时文件 | 样本数据整理过程中的临时名称列表 |
| `tmp_names_13.txt` | 临时文件 | 样本数据整理过程中的临时名称列表 |
| `执行SQL脚本.bat` | 辅助脚本 | Windows 下执行 SQL 初始化/修复脚本的批处理入口 |
| `用户结构示意图.html` | 说明文件 | 用户结构或关系的可视化示意页 |
| `系统测试与结果分析.md` | 项目文档 | 系统测试与结果分析说明 |

## 3. `docs/` 目录

`docs/` 保存的是较早期但仍有参考价值的项目资料。

| 路径 | 作用 |
| --- | --- |
| `docs/API文档.md` | 早期 API 接口整理文档，部分内容与当前实现有差异 |
| `docs/功能实现总结.md` | 阶段性功能完成情况总结 |
| `docs/后台管理系统说明.md` | 后台管理模块的独立说明 |
| `docs/技术文档引用.md` | 技术文档引用占位文件 |
| `docs/数据库设计.md` | 较早期数据库设计说明，含部分历史表结构 |
| `docs/样式风格.md` | 页面样式与视觉方向说明 |
| `docs/部署说明.md` | 项目部署说明 |
| `docs/项目结构说明.md` | 旧版项目结构说明 |

## 4. `src/main/java/com/gamestore/` 源码目录

### 4.1 根包与基础配置

| 路径 | 作用 |
| --- | --- |
| `src/main/java/com/gamestore/GameCommunityStoreApplication.java` | Spring Boot 主启动类 |
| `src/main/java/com/gamestore/config/StaticResourceConfig.java` | 上传文件静态映射配置，将本地上传目录映射为 `/uploads/**` |

### 4.2 `controller/` 控制器层

| 路径 | 作用 |
| --- | --- |
| `src/main/java/com/gamestore/controller/AdminCommunityController.java` | 后台统一管理论坛帖子、社区帖子和社区评论 |
| `src/main/java/com/gamestore/controller/AdminDashboardController.java` | 后台仪表盘数据概览接口 |
| `src/main/java/com/gamestore/controller/AdminGameController.java` | 后台游戏管理接口 |
| `src/main/java/com/gamestore/controller/AdminUserController.java` | 后台用户管理、角色/状态修改、密码重置接口 |
| `src/main/java/com/gamestore/controller/AuthController.java` | 注册、登录、退出登录、当前用户查询接口 |
| `src/main/java/com/gamestore/controller/BannerController.java` | 轮播图查询与管理接口 |
| `src/main/java/com/gamestore/controller/CategoryController.java` | 分类查询和分类 CRUD 接口 |
| `src/main/java/com/gamestore/controller/CommunityController.java` | 社区板块、社区帖子、社区评论接口 |
| `src/main/java/com/gamestore/controller/CommunityPageController.java` | 社区页面路由控制器 |
| `src/main/java/com/gamestore/controller/ContentRewardController.java` | 帖子/攻略打赏接口 |
| `src/main/java/com/gamestore/controller/FaviconController.java` | `favicon.ico` 请求处理 |
| `src/main/java/com/gamestore/controller/GameController.java` | 游戏列表、筛选、搜索、详情和多分类 CRUD 接口 |
| `src/main/java/com/gamestore/controller/GameDetailController.java` | 游戏详情页路由控制器 |
| `src/main/java/com/gamestore/controller/GameGuideController.java` | 攻略列表、详情、发布等接口 |
| `src/main/java/com/gamestore/controller/GuidePageController.java` | 攻略页面路由控制器 |
| `src/main/java/com/gamestore/controller/HealthController.java` | 健康检查接口 |
| `src/main/java/com/gamestore/controller/HomeController.java` | 首页、登录、注册页面路由 |
| `src/main/java/com/gamestore/controller/InnovationController.java` | 推荐、购前分析、成长、签到、行为日志接口 |
| `src/main/java/com/gamestore/controller/PageController.java` | 个人中心、后台、购物车、订单、游戏库、积分商城页面路由 |
| `src/main/java/com/gamestore/controller/PostController.java` | 讨论广场帖子、评论、图片上传接口 |
| `src/main/java/com/gamestore/controller/ProfileController.java` | 个人中心汇总、资料修改、密码修改接口 |
| `src/main/java/com/gamestore/controller/StoreController.java` | 购物车、结算、订单、游戏库、积分商城接口 |

### 4.3 `dto/request/` 请求 DTO

| 路径 | 作用 |
| --- | --- |
| `src/main/java/com/gamestore/dto/request/AddToCartRequest.java` | 加入购物车请求体 |
| `src/main/java/com/gamestore/dto/request/BehaviorEventRequest.java` | 行为日志上报请求体 |
| `src/main/java/com/gamestore/dto/request/ChangePasswordRequest.java` | 修改密码请求体 |
| `src/main/java/com/gamestore/dto/request/CheckoutRequest.java` | 订单结算请求体 |
| `src/main/java/com/gamestore/dto/request/ContentRewardRequest.java` | 内容打赏请求体 |
| `src/main/java/com/gamestore/dto/request/CreateCommentRequest.java` | 评论/回复请求体 |
| `src/main/java/com/gamestore/dto/request/CreateGameGuideRequest.java` | 发布攻略请求体 |
| `src/main/java/com/gamestore/dto/request/CreatePostRequest.java` | 发帖请求体，同时被论坛和社区板块复用 |
| `src/main/java/com/gamestore/dto/request/LoginRequest.java` | 登录请求体 |
| `src/main/java/com/gamestore/dto/request/RegisterRequest.java` | 注册请求体 |
| `src/main/java/com/gamestore/dto/request/UpdateCartItemRequest.java` | 更新购物车项请求体 |
| `src/main/java/com/gamestore/dto/request/UpdateProfileRequest.java` | 修改个人资料请求体 |

### 4.4 `dto/response/` 响应 DTO

| 路径 | 作用 |
| --- | --- |
| `src/main/java/com/gamestore/dto/response/AdminCommunityPostResponse.java` | 后台统一展示论坛贴和社区贴的管理 DTO |
| `src/main/java/com/gamestore/dto/response/ApiResponse.java` | 全局统一 API 响应对象 |
| `src/main/java/com/gamestore/dto/response/AuthResponse.java` | 认证响应 DTO，当前主线接口使用较少，属于通用保留类 |
| `src/main/java/com/gamestore/dto/response/BadgeResponse.java` | 勋章 DTO |
| `src/main/java/com/gamestore/dto/response/CartItemResponse.java` | 购物车项 DTO |
| `src/main/java/com/gamestore/dto/response/CartSummaryResponse.java` | 购物车汇总 DTO |
| `src/main/java/com/gamestore/dto/response/CheckInResponse.java` | 每日签到结果 DTO |
| `src/main/java/com/gamestore/dto/response/CheckoutOptionsResponse.java` | 结算页面可用配置 DTO |
| `src/main/java/com/gamestore/dto/response/CommentResponse.java` | 论坛评论 DTO |
| `src/main/java/com/gamestore/dto/response/ContentRewardActionResponse.java` | 打赏完成后的返回 DTO |
| `src/main/java/com/gamestore/dto/response/ContentRewardSummaryResponse.java` | 打赏汇总 DTO |
| `src/main/java/com/gamestore/dto/response/DecisionInsightResponse.java` | 购前决策分析 DTO |
| `src/main/java/com/gamestore/dto/response/GameGuideResponse.java` | 攻略详情/列表 DTO |
| `src/main/java/com/gamestore/dto/response/GameWithCategoriesDTO.java` | 带多分类信息的游戏 DTO |
| `src/main/java/com/gamestore/dto/response/GrowthDashboardResponse.java` | 成长面板 DTO |
| `src/main/java/com/gamestore/dto/response/GrowthTaskResponse.java` | 成长任务 DTO |
| `src/main/java/com/gamestore/dto/response/LibraryGameResponse.java` | 游戏库 DTO |
| `src/main/java/com/gamestore/dto/response/OrderItemResponse.java` | 订单项 DTO |
| `src/main/java/com/gamestore/dto/response/OrderResponse.java` | 订单 DTO |
| `src/main/java/com/gamestore/dto/response/OwnedDiscountCardResponse.java` | 用户已持有折扣卡 DTO |
| `src/main/java/com/gamestore/dto/response/PointShopItemResponse.java` | 积分商城商品 DTO |
| `src/main/java/com/gamestore/dto/response/PointTransactionResponse.java` | 积分流水 DTO |
| `src/main/java/com/gamestore/dto/response/PostResponse.java` | 论坛帖子 DTO |
| `src/main/java/com/gamestore/dto/response/ProfileSummaryResponse.java` | 个人中心汇总 DTO |
| `src/main/java/com/gamestore/dto/response/RecommendationReasonDetail.java` | 推荐理由细节 DTO |
| `src/main/java/com/gamestore/dto/response/RecommendationResponse.java` | 推荐结果 DTO |
| `src/main/java/com/gamestore/dto/response/RewardSupporterResponse.java` | 打赏支持者 DTO |

### 4.5 `entity/` 实体层

| 路径 | 作用 |
| --- | --- |
| `src/main/java/com/gamestore/entity/Banner.java` | 轮播图实体 |
| `src/main/java/com/gamestore/entity/CartItem.java` | 购物车实体 |
| `src/main/java/com/gamestore/entity/Category.java` | 分类实体 |
| `src/main/java/com/gamestore/entity/Comment.java` | 论坛评论实体 |
| `src/main/java/com/gamestore/entity/CommunityComment.java` | 社区评论实体 |
| `src/main/java/com/gamestore/entity/CommunityPost.java` | 社区帖子实体 |
| `src/main/java/com/gamestore/entity/CommunitySection.java` | 社区板块实体 |
| `src/main/java/com/gamestore/entity/ContentReward.java` | 内容打赏记录实体 |
| `src/main/java/com/gamestore/entity/Game.java` | 游戏实体，含多分类关联 |
| `src/main/java/com/gamestore/entity/GameGuide.java` | 游戏攻略实体 |
| `src/main/java/com/gamestore/entity/GameOrder.java` | 订单实体 |
| `src/main/java/com/gamestore/entity/GameOrderItem.java` | 订单项实体 |
| `src/main/java/com/gamestore/entity/PointTransaction.java` | 积分流水实体 |
| `src/main/java/com/gamestore/entity/Post.java` | 论坛帖子实体 |
| `src/main/java/com/gamestore/entity/User.java` | 用户实体 |
| `src/main/java/com/gamestore/entity/UserBadge.java` | 用户勋章实体 |
| `src/main/java/com/gamestore/entity/UserBehaviorLog.java` | 用户行为日志实体 |
| `src/main/java/com/gamestore/entity/UserDiscountCard.java` | 用户折扣卡实体 |
| `src/main/java/com/gamestore/entity/UserGame.java` | 用户游戏库实体 |
| `src/main/java/com/gamestore/entity/UserSession.java` | 用户会话实体 |

### 4.6 `exception/` 异常层

| 路径 | 作用 |
| --- | --- |
| `src/main/java/com/gamestore/exception/CustomException.java` | 自定义业务异常 |
| `src/main/java/com/gamestore/exception/GlobalExceptionHandler.java` | 统一异常处理与错误响应 |

### 4.7 `repository/` 数据访问层

| 路径 | 作用 |
| --- | --- |
| `src/main/java/com/gamestore/repository/BannerRepository.java` | 轮播图数据访问 |
| `src/main/java/com/gamestore/repository/CartItemRepository.java` | 购物车数据访问 |
| `src/main/java/com/gamestore/repository/CategoryRepository.java` | 分类数据访问 |
| `src/main/java/com/gamestore/repository/CommentRepository.java` | 论坛评论数据访问 |
| `src/main/java/com/gamestore/repository/CommunityCommentRepository.java` | 社区评论数据访问 |
| `src/main/java/com/gamestore/repository/CommunityPostRepository.java` | 社区帖子数据访问 |
| `src/main/java/com/gamestore/repository/CommunitySectionRepository.java` | 社区板块数据访问 |
| `src/main/java/com/gamestore/repository/ContentRewardRepository.java` | 打赏记录数据访问 |
| `src/main/java/com/gamestore/repository/GameGuideRepository.java` | 攻略数据访问 |
| `src/main/java/com/gamestore/repository/GameOrderItemRepository.java` | 订单项数据访问 |
| `src/main/java/com/gamestore/repository/GameOrderRepository.java` | 订单数据访问 |
| `src/main/java/com/gamestore/repository/GameRepository.java` | 游戏数据访问 |
| `src/main/java/com/gamestore/repository/PointTransactionRepository.java` | 积分流水数据访问 |
| `src/main/java/com/gamestore/repository/PostRepository.java` | 论坛帖子数据访问 |
| `src/main/java/com/gamestore/repository/UserBadgeRepository.java` | 用户勋章数据访问 |
| `src/main/java/com/gamestore/repository/UserBehaviorLogRepository.java` | 行为日志数据访问 |
| `src/main/java/com/gamestore/repository/UserDiscountCardRepository.java` | 折扣卡数据访问 |
| `src/main/java/com/gamestore/repository/UserGameRepository.java` | 游戏库数据访问 |
| `src/main/java/com/gamestore/repository/UserRepository.java` | 用户数据访问 |
| `src/main/java/com/gamestore/repository/UserSessionRepository.java` | 会话数据访问 |

### 4.8 `service/` 业务服务层

| 路径 | 作用 |
| --- | --- |
| `src/main/java/com/gamestore/service/AuthService.java` | 认证与会话核心逻辑 |
| `src/main/java/com/gamestore/service/BannerService.java` | 轮播图管理逻辑 |
| `src/main/java/com/gamestore/service/CategoryService.java` | 分类管理逻辑 |
| `src/main/java/com/gamestore/service/CommunityService.java` | 社区板块、帖子、评论逻辑 |
| `src/main/java/com/gamestore/service/ContentRewardService.java` | 内容打赏、积分扣加、榜单统计逻辑 |
| `src/main/java/com/gamestore/service/CurrentUserService.java` | 当前登录用户提取逻辑 |
| `src/main/java/com/gamestore/service/GameGuideService.java` | 攻略发布与查询逻辑 |
| `src/main/java/com/gamestore/service/GameService.java` | 游戏列表、详情、多分类、搜索、后台维护逻辑 |
| `src/main/java/com/gamestore/service/InnovationService.java` | 推荐、购前分析、成长、签到、勋章主逻辑 |
| `src/main/java/com/gamestore/service/LegacySchemaRepairRunner.java` | 启动时旧库结构修补逻辑 |
| `src/main/java/com/gamestore/service/PasswordService.java` | 密码加密与升级逻辑 |
| `src/main/java/com/gamestore/service/PostImageStorageService.java` | 帖子图片上传保存逻辑 |
| `src/main/java/com/gamestore/service/PostService.java` | 讨论广场帖子/评论逻辑 |
| `src/main/java/com/gamestore/service/StoreService.java` | 购物车、结算、订单、积分商城主逻辑 |
| `src/main/java/com/gamestore/service/UserService.java` | 用户资料与密码管理逻辑 |

### 4.9 `util/` 工具层

| 路径 | 作用 |
| --- | --- |
| `src/main/java/com/gamestore/util/GameNameFormatter.java` | 游戏名称格式化工具 |
| `src/main/java/com/gamestore/util/ResponseUtil.java` | 统一响应组装工具 |

## 5. `src/main/resources/` 资源目录

### 5.1 配置文件

| 路径 | 作用 |
| --- | --- |
| `src/main/resources/application.properties` | 应用核心配置，包括数据库、JPA、上传、日志、Thymeleaf 等 |

### 5.2 `templates/` Thymeleaf 页面

| 路径 | 作用 |
| --- | --- |
| `src/main/resources/templates/admin/banners.html` | 轮播图管理页 |
| `src/main/resources/templates/admin/index.html` | 后台管理主页面 |
| `src/main/resources/templates/cart.html` | 购物车页面 |
| `src/main/resources/templates/community/index.html` | 社区首页页面 |
| `src/main/resources/templates/game-detail.html` | 游戏详情页面 |
| `src/main/resources/templates/guides/create.html` | 发布攻略页面 |
| `src/main/resources/templates/guides/detail.html` | 攻略详情页面 |
| `src/main/resources/templates/guides/index.html` | 攻略列表页面 |
| `src/main/resources/templates/index.html` | 首页商城页面，也是前台聚合入口 |
| `src/main/resources/templates/index.html.bak` | 首页旧版/备份模板 |
| `src/main/resources/templates/library.html` | 用户游戏库页面 |
| `src/main/resources/templates/login.html` | 登录页面 |
| `src/main/resources/templates/orders.html` | 订单页面 |
| `src/main/resources/templates/points-shop.html` | 积分商城页面 |
| `src/main/resources/templates/profile.html` | 个人中心页面 |
| `src/main/resources/templates/register.html` | 注册页面 |

### 5.3 `static/` 静态页面与脚本

| 路径 | 作用 |
| --- | --- |
| `src/main/resources/static/.well-known/appspecific/com.chrome.devtools.json` | DevTools 相关的应用配置文件，不属于业务逻辑 |
| `src/main/resources/static/create-post.html` | 讨论广场发帖页面 |
| `src/main/resources/static/forum.html` | 讨论广场列表页面 |
| `src/main/resources/static/post-detail.html` | 讨论广场帖子详情、评论、打赏页面 |
| `src/main/resources/static/js/admin/banner.js` | 轮播图管理辅助脚本/旧版脚本 |
| `src/main/resources/static/js/admin/banners.js` | 轮播图管理主脚本 |
| `src/main/resources/static/js/admin/categories.js` | 分类管理脚本 |
| `src/main/resources/static/js/admin/community.js` | 社区管理脚本 |
| `src/main/resources/static/js/admin/dashboard.js` | 后台仪表盘脚本 |
| `src/main/resources/static/js/admin/games.js` | 游戏管理脚本 |
| `src/main/resources/static/js/admin/users.js` | 用户管理脚本 |
| `src/main/resources/static/js/forum-post-content.js` | 论坛帖子内容图片与 Markdown 渲染工具 |
| `src/main/resources/static/js/ui-feedback.js` | 全站提示与确认对话框脚本 |

### 5.4 批量资源目录

| 路径 | 说明 |
| --- | --- |
| `src/main/resources/static/image/` | 共 92 个图片素材文件，主要用于首页、详情页、论坛/社区演示的游戏封面和截图资源 |

## 6. `src/test/java/` 测试目录

| 路径 | 作用 |
| --- | --- |
| `src/test/java/com/gamestore/GameCommunityStoreApplicationTests.java` | 应用上下文启动测试 |
| `src/test/java/com/gamestore/service/GameServiceTest.java` | `GameService` 单元测试/Mock 测试 |

## 7. `开发文档/` 目录

### 7.1 说明文档

| 路径 | 作用 |
| --- | --- |
| `开发文档/A_项目功能实现框架技术栈说明.md` | 本次新增的项目功能、框架与技术栈总览文档 |
| `开发文档/B_功能实现代码文件与行数清单.md` | 本次新增的功能模块与代码文件/行数清单 |
| `开发文档/C_项目目录与文件功能说明.md` | 本次新增的项目目录与文件说明文档 |
| `开发文档/开发计划及进度.md` | 开发计划、阶段进度记录 |
| `开发文档/数据库结构.md` | 当前主线数据库结构说明 |
| `开发文档/数据库表结构总表.md` | 数据库表结构总览 |
| `开发文档/运行与维护.md` | 运行、维护、日常操作说明 |
| `开发文档/部署文档.md` | 部署流程文档 |
| `开发文档/项目整体架构说明.md` | 较完整的架构与功能说明文档 |
| `开发文档/项目框架.md` | 项目框架概述文档 |

### 7.2 `SQL脚本/` 初始化与迁移脚本

| 路径 | 作用 |
| --- | --- |
| `开发文档/SQL脚本/01_数据库初始化.sql` | 创建主线业务表结构 |
| `开发文档/SQL脚本/02_基础演示数据.sql` | 注入基础演示数据 |
| `开发文档/SQL脚本/03_结构修复与数据整理.sql` | 修复旧环境结构并整理历史数据 |
| `开发文档/SQL脚本/04_创新功能扩展.sql` | 为推荐/成长等创新功能补充结构 |
| `开发文档/SQL脚本/05_分类扩展与现有游戏类型补充.sql` | 分类扩展与游戏分类补全 |
| `开发文档/SQL脚本/06_游戏样本2_100个商店游戏.sql` | 批量插入商店游戏样本 |
| `开发文档/SQL脚本/07_论坛样本1_100个帖子.sql` | 批量插入论坛样本帖子 |
| `开发文档/SQL脚本/08_重复样本去重清理.sql` | 清理重复样本和脏数据 |
| `开发文档/SQL脚本/09_移除社区开发工具板块.sql` | 移除不再作为主展示内容的社区板块 |
| `开发文档/SQL脚本/10_讨论广场帖子支持图片.sql` | 为论坛帖子增加图片支持 |
| `开发文档/SQL脚本/11_修正样本2游戏名.sql` | 修正游戏样本名称 |
| `开发文档/SQL脚本/12_热门人气游戏_100个样本.sql` | 热门游戏样本导入 |
| `开发文档/SQL脚本/13_热门游戏样本2.sql` | 第二组热门游戏样本导入 |
| `开发文档/SQL脚本/14_游戏攻略系统.sql` | 攻略系统表结构扩展 |
| `开发文档/SQL脚本/15_游戏攻略示例数据.sql` | 攻略示例数据导入 |
| `开发文档/SQL脚本/16_社区内容打赏功能.sql` | 内容打赏功能表结构补充 |

### 7.3 `sql/` 数据导出文件

| 路径 | 作用 |
| --- | --- |
| `开发文档/sql/README.txt` | 导出 SQL 文件说明 |
| `开发文档/sql/bishe_data_only_notablespaces_20260416_083830.sql` | 仅数据导出文件 |
| `开发文档/sql/bishe_full_notablespaces_20260416_083830.sql` | 完整库导出文件 |
| `开发文档/sql/bishe_table_counts_20260416_083800.tsv` | 表行数统计结果 |

## 8. 论文与配套材料目录

### 8.1 `论文文档/` 顶层说明

`论文文档/` 共 136 个文件，主要用于毕业论文写作、图表生成、校方材料整理和终稿输出，不参与系统运行。

### 8.2 项目撰写与图表生成相关文件

| 路径 | 作用 |
| --- | --- |
| `论文文档/3.3概念架构设计-正式版.md` | 论文中概念架构设计章节草稿 |
| `论文文档/4相关技术与开发环境-正式版.md` | 论文技术与环境章节草稿 |
| `论文文档/5系统实现-正式版.md` | 论文系统实现章节草稿 |
| `论文文档/6系统测试与结果分析-正式版.md` | 论文测试章节草稿 |
| `论文文档/build_final_thesis.ps1` | 论文最终稿构建脚本 |
| `论文文档/extract_doc_text.py` | 从文档中提取文本的脚本 |
| `论文文档/generate_er_htmls.py` | 生成 E-R 图 HTML 的脚本 |
| `论文文档/generate_final_thesis.py` | 生成论文终稿的脚本 |
| `论文文档/er-chen-notation.css` | E-R 图样式文件 |
| `论文文档/er-edge-editor.css` | 图表编辑样式文件 |
| `论文文档/er-edge-editor.js` | 图表编辑脚本 |
| `论文文档/开题材料汇总.md` | 开题相关材料整理说明 |
| `论文文档/项目架构与组成说明-详细版.md` | 项目架构与组成详细稿 |
| `论文文档/降AIGC率方法总结.md` | 论文写作过程中的降 AIGC 记录 |

### 8.3 批量材料目录

| 路径 | 说明 |
| --- | --- |
| `论文文档/完整的校方资料/` | 校方通知、模板、评价规范、手册、答辩/开题表格等资料目录 |
| `论文文档/终稿/` | 毕业论文终稿、修订脚本、替换正文文本、输出文档、插图 HTML |
| `论文文档/终稿/插图HTML/` | 论文插图 HTML 文件集合 |
| `论文文档/图*.html` | 系统架构图、E-R 图、流程图等论文插图页面 |

## 9. IDE、缓存、构建产物与批量目录归并说明

| 路径 | 当前规模 | 说明 |
| --- | ---: | --- |
| `.idea/` | 16 个文件 | IntelliJ IDEA 工程配置、数据库连接配置和本地工作区信息 |
| `.vscode/` | 2 个文件 | VS Code 扩展和调试配置 |
| `.mvn/` | 1 个配置目录 | Maven Wrapper 配置目录 |
| `.maven-home/` + `.maven-user-home/` | 193 个文件 | Maven Wrapper 下载的本地 Maven 运行时与缓存 |
| `.m2repo/` | 1250 个文件 | 本地 Maven 依赖仓库缓存，非业务代码 |
| `target/` | 280 个文件 | 编译产物、测试类、测试报告、打包中间结果 |
| `src/main/resources/static/image/` | 92 个文件 | 演示图片资源目录 |
| `.git/` | Git 元数据 | 版本控制内部目录，不属于项目业务内容 |

## 10. 结论

当前项目目录可以分成 5 层含义：

1. 根目录负责运行、构建、部署和少量辅助脚本。
2. `src/` 负责真正的系统实现代码与页面资源。
3. `docs/` 和 `开发文档/` 负责项目说明、数据库与部署文档。
4. `论文文档/` 负责毕业论文材料、图表和终稿输出。
5. `.idea`、`.m2repo`、`target` 等目录属于开发工具、依赖缓存和构建产物。

如果后续还要继续补全文档，最值得增加的是：

- `static/image/` 演示图片素材清单
- `论文文档/终稿/` 终稿脚本与输出物的更细分类索引
- `target/` 目录的构建产物说明和测试报告引用关系
