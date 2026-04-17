# B_功能实现代码文件与行数清单

## 1. 说明

- 统计时间：2026-04-17
- 统计方式：对 `src/main/java`、`src/main/resources`、`src/test/java` 下的代码/模板/脚本文件执行 `wc -l`
- 统计范围：`*.java`、`*.html`、`*.js`、`*.properties`、`*.json`
- 说明原则：按“功能模块 -> 代码文件 -> 代码行数”整理；共享文件会在多个功能模块中重复出现

## 2. 启动与通用基础设施

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/GameCommunityStoreApplication.java` | 13 | Spring Boot 启动入口 |
| `src/main/resources/application.properties` | 53 | 应用端口、数据源、JPA、上传目录、日志等核心配置 |
| `src/main/java/com/gamestore/config/StaticResourceConfig.java` | 31 | 把本地上传目录映射到 `/uploads/**` |
| `src/main/java/com/gamestore/service/LegacySchemaRepairRunner.java` | 258 | 启动时修复旧库结构、索引和外键兼容问题 |
| `src/main/java/com/gamestore/exception/CustomException.java` | 12 | 业务异常定义 |
| `src/main/java/com/gamestore/exception/GlobalExceptionHandler.java` | 50 | 全局异常处理与统一错误响应 |
| `src/main/java/com/gamestore/util/ResponseUtil.java` | 39 | 统一 API 返回格式 |
| `src/main/java/com/gamestore/controller/HealthController.java` | 15 | 健康检查接口 |
| `src/main/java/com/gamestore/controller/FaviconController.java` | 14 | `favicon.ico` 请求处理 |
| `src/main/resources/static/js/ui-feedback.js` | 314 | 全站复用的提示、弹窗、确认框交互组件 |

## 3. 用户认证与会话

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/controller/AuthController.java` | 161 | 注册、登录、退出、当前用户查询 API |
| `src/main/java/com/gamestore/service/AuthService.java` | 159 | 注册校验、登录态创建、Token 查用户、退出登录 |
| `src/main/java/com/gamestore/service/CurrentUserService.java` | 48 | 从 Header/Cookie 中提取当前登录用户 |
| `src/main/java/com/gamestore/service/PasswordService.java` | 75 | PBKDF2 密码加密、匹配与旧密码升级 |
| `src/main/java/com/gamestore/service/UserService.java` | 172 | 用户资料维护、密码修改、密码重置、积分更新 |
| `src/main/java/com/gamestore/entity/User.java` | 180 | 用户实体与角色/状态定义 |
| `src/main/java/com/gamestore/entity/UserSession.java` | 114 | 登录会话实体 |
| `src/main/java/com/gamestore/repository/UserRepository.java` | 29 | 用户查询与存在性校验 |
| `src/main/java/com/gamestore/repository/UserSessionRepository.java` | 31 | Session Token 持久化与过期清理 |
| `src/main/java/com/gamestore/dto/request/RegisterRequest.java` | 51 | 注册请求 DTO |
| `src/main/java/com/gamestore/dto/request/LoginRequest.java` | 31 | 登录请求 DTO |
| `src/main/resources/templates/login.html` | 179 | 登录页模板与登录脚本 |
| `src/main/resources/templates/register.html` | 216 | 注册页模板与注册脚本 |

## 4. 首页商城、游戏目录、详情、分类与轮播

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/controller/HomeController.java` | 23 | 首页、登录页、注册页路由入口 |
| `src/main/java/com/gamestore/controller/GameController.java` | 251 | 游戏列表、筛选、搜索、详情、多分类 CRUD API |
| `src/main/java/com/gamestore/controller/GameDetailController.java` | 25 | 游戏详情页面路由 |
| `src/main/java/com/gamestore/controller/CategoryController.java` | 75 | 分类列表、根分类、子分类、分类 CRUD API |
| `src/main/java/com/gamestore/controller/BannerController.java` | 98 | 轮播图查询、创建、更新、删除、启停 API |
| `src/main/java/com/gamestore/service/GameService.java` | 430 | 游戏查询、创建、更新、分类绑定、搜索、精选逻辑 |
| `src/main/java/com/gamestore/service/CategoryService.java` | 122 | 分类树与分类维护逻辑 |
| `src/main/java/com/gamestore/service/BannerService.java` | 129 | 轮播图管理逻辑 |
| `src/main/java/com/gamestore/entity/Game.java` | 316 | 游戏实体，包含多分类关联、价格、评分、标签等 |
| `src/main/java/com/gamestore/entity/Category.java` | 144 | 分类实体，支持父子层级 |
| `src/main/java/com/gamestore/entity/Banner.java` | 153 | 轮播图实体 |
| `src/main/java/com/gamestore/repository/GameRepository.java` | 141 | 游戏筛选、模糊搜索、精选查询等数据访问 |
| `src/main/java/com/gamestore/repository/CategoryRepository.java` | 23 | 分类数据访问 |
| `src/main/java/com/gamestore/repository/BannerRepository.java` | 30 | 轮播图数据访问 |
| `src/main/java/com/gamestore/dto/response/GameWithCategoriesDTO.java` | 265 | 游戏详情与多分类返回 DTO |
| `src/main/resources/templates/index.html` | 2180 | 首页模板，包含商城展示、轮播、推荐区和导航交互 |
| `src/main/resources/templates/game-detail.html` | 1152 | 游戏详情模板，包含加购/领取与购前分析交互 |

## 5. 商城交易闭环与积分商城

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/controller/StoreController.java` | 184 | 购物车、结算、订单、游戏库、积分商城 API |
| `src/main/java/com/gamestore/service/StoreService.java` | 749 | 商城主业务：加购、结算、生成订单、入库、积分、折扣卡 |
| `src/main/java/com/gamestore/util/GameNameFormatter.java` | 89 | 订单/游戏库等场景的游戏名称格式化 |
| `src/main/java/com/gamestore/entity/CartItem.java` | 121 | 购物车实体 |
| `src/main/java/com/gamestore/entity/GameOrder.java` | 205 | 订单实体 |
| `src/main/java/com/gamestore/entity/GameOrderItem.java` | 122 | 订单项实体 |
| `src/main/java/com/gamestore/entity/UserGame.java` | 103 | 用户游戏库实体 |
| `src/main/java/com/gamestore/entity/PointTransaction.java` | 116 | 积分流水实体 |
| `src/main/java/com/gamestore/entity/UserDiscountCard.java` | 161 | 用户折扣卡实体 |
| `src/main/java/com/gamestore/repository/CartItemRepository.java` | 22 | 购物车数据访问 |
| `src/main/java/com/gamestore/repository/GameOrderRepository.java` | 26 | 订单数据访问 |
| `src/main/java/com/gamestore/repository/GameOrderItemRepository.java` | 13 | 订单项数据访问 |
| `src/main/java/com/gamestore/repository/UserGameRepository.java` | 17 | 游戏库数据访问 |
| `src/main/java/com/gamestore/repository/PointTransactionRepository.java` | 13 | 积分流水数据访问 |
| `src/main/java/com/gamestore/repository/UserDiscountCardRepository.java` | 16 | 折扣卡数据访问 |
| `src/main/java/com/gamestore/dto/request/AddToCartRequest.java` | 23 | 加入购物车请求 DTO |
| `src/main/java/com/gamestore/dto/request/UpdateCartItemRequest.java` | 23 | 更新购物车请求 DTO |
| `src/main/java/com/gamestore/dto/request/CheckoutRequest.java` | 32 | 结算请求 DTO |
| `src/main/java/com/gamestore/dto/response/CartItemResponse.java` | 18 | 购物车项返回 DTO |
| `src/main/java/com/gamestore/dto/response/CartSummaryResponse.java` | 12 | 购物车汇总 DTO |
| `src/main/java/com/gamestore/dto/response/CheckoutOptionsResponse.java` | 11 | 结算可用积分/消费卡配置 DTO |
| `src/main/java/com/gamestore/dto/response/OrderItemResponse.java` | 14 | 订单项返回 DTO |
| `src/main/java/com/gamestore/dto/response/OrderResponse.java` | 23 | 订单返回 DTO |
| `src/main/java/com/gamestore/dto/response/LibraryGameResponse.java` | 17 | 游戏库返回 DTO |
| `src/main/java/com/gamestore/dto/response/PointTransactionResponse.java` | 13 | 积分流水返回 DTO |
| `src/main/java/com/gamestore/dto/response/PointShopItemResponse.java` | 13 | 积分商城商品 DTO |
| `src/main/java/com/gamestore/dto/response/OwnedDiscountCardResponse.java` | 16 | 用户兑换后持有的折扣卡 DTO |
| `src/main/resources/templates/cart.html` | 677 | 购物车页面 |
| `src/main/resources/templates/orders.html` | 340 | 订单列表页面 |
| `src/main/resources/templates/library.html` | 355 | 用户游戏库页面 |
| `src/main/resources/templates/points-shop.html` | 467 | 积分商城与积分兑换页面 |

## 6. 个人中心

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/controller/ProfileController.java` | 112 | 个人中心汇总、资料修改、密码修改 API |
| `src/main/java/com/gamestore/service/UserService.java` | 172 | 资料更新、密码变更、重置密码等用户逻辑 |
| `src/main/java/com/gamestore/service/StoreService.java` | 749 | 为个人中心提供订单数、游戏库数、积分流水 |
| `src/main/java/com/gamestore/dto/request/UpdateProfileRequest.java` | 23 | 更新资料请求 DTO |
| `src/main/java/com/gamestore/dto/request/ChangePasswordRequest.java` | 32 | 修改密码请求 DTO |
| `src/main/java/com/gamestore/dto/response/ProfileSummaryResponse.java` | 19 | 个人中心汇总 DTO |
| `src/main/resources/templates/profile.html` | 687 | 个人中心页面，整合账号、统计、成长和签到功能 |

## 7. 讨论广场

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/controller/PostController.java` | 185 | 讨论广场帖子、评论、搜索、图片上传 API |
| `src/main/java/com/gamestore/service/PostService.java` | 355 | 讨论广场帖子/评论业务逻辑 |
| `src/main/java/com/gamestore/service/PostImageStorageService.java` | 96 | 论坛图片保存、数量/格式/大小校验 |
| `src/main/java/com/gamestore/entity/Post.java` | 247 | 讨论广场帖子实体 |
| `src/main/java/com/gamestore/entity/Comment.java` | 182 | 讨论广场评论实体 |
| `src/main/java/com/gamestore/repository/PostRepository.java` | 94 | 帖子数据访问 |
| `src/main/java/com/gamestore/repository/CommentRepository.java` | 63 | 评论数据访问 |
| `src/main/java/com/gamestore/dto/request/CreatePostRequest.java` | 69 | 发帖请求 DTO |
| `src/main/java/com/gamestore/dto/request/CreateCommentRequest.java` | 38 | 评论请求 DTO |
| `src/main/java/com/gamestore/dto/response/PostResponse.java` | 217 | 帖子详情/列表返回 DTO |
| `src/main/java/com/gamestore/dto/response/CommentResponse.java` | 138 | 评论返回 DTO |
| `src/main/resources/static/forum.html` | 559 | 讨论广场列表页 |
| `src/main/resources/static/create-post.html` | 528 | 发帖页面 |
| `src/main/resources/static/post-detail.html` | 807 | 帖子详情、评论、打赏页面 |
| `src/main/resources/static/js/forum-post-content.js` | 120 | 帖子内容中的图片/Markdown 安全解析与摘要处理 |

## 8. 社区板块

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/controller/CommunityPageController.java` | 56 | 社区首页路由，预留 section/post/create 页面扩展位 |
| `src/main/java/com/gamestore/controller/CommunityController.java` | 199 | 板块、帖子、评论、精华贴、用户内容 API |
| `src/main/java/com/gamestore/service/CommunityService.java` | 335 | 社区板块、帖子、评论、管理员社区管理逻辑 |
| `src/main/java/com/gamestore/entity/CommunitySection.java` | 143 | 社区板块实体 |
| `src/main/java/com/gamestore/entity/CommunityPost.java` | 241 | 社区帖子实体 |
| `src/main/java/com/gamestore/entity/CommunityComment.java` | 164 | 社区评论实体 |
| `src/main/java/com/gamestore/repository/CommunitySectionRepository.java` | 18 | 板块数据访问 |
| `src/main/java/com/gamestore/repository/CommunityPostRepository.java` | 53 | 社区帖子数据访问 |
| `src/main/java/com/gamestore/repository/CommunityCommentRepository.java` | 33 | 社区评论数据访问 |
| `src/main/resources/templates/community/index.html` | 540 | 社区首页模板，展示板块卡片、论坛统计和攻略入口 |

## 9. 游戏攻略

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/controller/GuidePageController.java` | 28 | 攻略列表、发布页、详情页路由 |
| `src/main/java/com/gamestore/controller/GameGuideController.java` | 78 | 攻略列表、精选、数量、创建、详情 API |
| `src/main/java/com/gamestore/service/GameGuideService.java` | 153 | 攻略筛选、创建、浏览数更新等逻辑 |
| `src/main/java/com/gamestore/entity/GameGuide.java` | 257 | 攻略实体 |
| `src/main/java/com/gamestore/repository/GameGuideRepository.java` | 59 | 攻略搜索和精选查询 |
| `src/main/java/com/gamestore/dto/request/CreateGameGuideRequest.java` | 79 | 发布攻略请求 DTO |
| `src/main/java/com/gamestore/dto/response/GameGuideResponse.java` | 216 | 攻略返回 DTO |
| `src/main/resources/templates/guides/index.html` | 747 | 攻略列表页 |
| `src/main/resources/templates/guides/create.html` | 480 | 发布攻略页 |
| `src/main/resources/templates/guides/detail.html` | 790 | 攻略详情与攻略打赏页面 |

## 10. 内容打赏

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/controller/ContentRewardController.java` | 86 | 论坛帖子/攻略打赏与打赏名单 API |
| `src/main/java/com/gamestore/service/ContentRewardService.java` | 275 | 打赏扣分、加分、流水记录、支持者排行聚合逻辑 |
| `src/main/java/com/gamestore/entity/ContentReward.java` | 107 | 内容打赏实体 |
| `src/main/java/com/gamestore/repository/ContentRewardRepository.java` | 16 | 打赏记录查询 |
| `src/main/java/com/gamestore/dto/request/ContentRewardRequest.java` | 14 | 打赏请求 DTO |
| `src/main/java/com/gamestore/dto/response/ContentRewardActionResponse.java` | 50 | 打赏结果返回 DTO |
| `src/main/java/com/gamestore/dto/response/ContentRewardSummaryResponse.java` | 34 | 打赏汇总返回 DTO |
| `src/main/java/com/gamestore/dto/response/RewardSupporterResponse.java` | 52 | 支持者榜单 DTO |
| `src/main/resources/static/post-detail.html` | 807 | 帖子详情页中的帖子打赏前端实现 |
| `src/main/resources/templates/guides/detail.html` | 790 | 攻略详情页中的攻略打赏前端实现 |

## 11. 创新推荐、购前分析、成长与行为日志

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/controller/InnovationController.java` | 105 | 推荐列表、购前分析、成长面板、签到、行为上报 API |
| `src/main/java/com/gamestore/service/InnovationService.java` | 1405 | 推荐排序、兴趣画像、购前分析、勋章计算、签到、任务与缓存主逻辑 |
| `src/main/java/com/gamestore/entity/UserBehaviorLog.java` | 126 | 用户行为日志实体 |
| `src/main/java/com/gamestore/entity/UserBadge.java` | 99 | 用户勋章实体 |
| `src/main/java/com/gamestore/repository/UserBehaviorLogRepository.java` | 32 | 行为日志查询 |
| `src/main/java/com/gamestore/repository/UserBadgeRepository.java` | 15 | 用户勋章查询 |
| `src/main/java/com/gamestore/dto/request/BehaviorEventRequest.java` | 41 | 前端行为上报请求 DTO |
| `src/main/java/com/gamestore/dto/response/RecommendationResponse.java` | 22 | 推荐结果 DTO |
| `src/main/java/com/gamestore/dto/response/RecommendationReasonDetail.java` | 7 | 推荐理由细项 DTO |
| `src/main/java/com/gamestore/dto/response/DecisionInsightResponse.java` | 18 | 购前分析结果 DTO |
| `src/main/java/com/gamestore/dto/response/GrowthDashboardResponse.java` | 17 | 成长面板 DTO |
| `src/main/java/com/gamestore/dto/response/GrowthTaskResponse.java` | 11 | 成长任务 DTO |
| `src/main/java/com/gamestore/dto/response/CheckInResponse.java` | 8 | 签到结果 DTO |
| `src/main/java/com/gamestore/dto/response/BadgeResponse.java` | 11 | 勋章 DTO |
| `src/main/resources/templates/index.html` | 2180 | 首页中的推荐列表与聚合入口前端实现 |
| `src/main/resources/templates/game-detail.html` | 1152 | 游戏详情页中的行为上报与购前分析展示 |
| `src/main/resources/templates/profile.html` | 687 | 个人中心中的成长面板与签到前端实现 |

## 12. 后台管理

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/main/java/com/gamestore/controller/PageController.java` | 42 | 后台页、购物车、订单、游戏库、积分商城等页面路由 |
| `src/main/java/com/gamestore/controller/AdminDashboardController.java` | 65 | 后台仪表盘统计 API |
| `src/main/java/com/gamestore/controller/AdminGameController.java` | 101 | 后台游戏管理 API |
| `src/main/java/com/gamestore/controller/AdminUserController.java` | 139 | 后台用户管理 API |
| `src/main/java/com/gamestore/controller/AdminCommunityController.java` | 240 | 后台社区/论坛内容管理 API |
| `src/main/java/com/gamestore/controller/BannerController.java` | 98 | 后台轮播图管理 API |
| `src/main/java/com/gamestore/controller/CategoryController.java` | 75 | 后台分类管理复用 API |
| `src/main/java/com/gamestore/service/GameService.java` | 430 | 后台游戏增删改查的核心服务 |
| `src/main/java/com/gamestore/service/UserService.java` | 172 | 后台用户资料、角色、密码重置的核心服务 |
| `src/main/java/com/gamestore/service/CommunityService.java` | 335 | 后台社区板块帖子/评论管理逻辑 |
| `src/main/java/com/gamestore/service/PostService.java` | 355 | 后台论坛帖子管理逻辑 |
| `src/main/java/com/gamestore/service/BannerService.java` | 129 | 后台轮播图管理逻辑 |
| `src/main/resources/templates/admin/index.html` | 579 | 后台主页面与侧边导航框架 |
| `src/main/resources/templates/admin/banners.html` | 154 | 轮播图管理页面模板 |
| `src/main/resources/static/js/admin/dashboard.js` | 53 | 后台仪表盘前端逻辑 |
| `src/main/resources/static/js/admin/games.js` | 592 | 后台游戏管理前端逻辑 |
| `src/main/resources/static/js/admin/users.js` | 308 | 后台用户管理前端逻辑 |
| `src/main/resources/static/js/admin/community.js` | 269 | 后台社区管理前端逻辑 |
| `src/main/resources/static/js/admin/categories.js` | 275 | 后台分类管理前端逻辑 |
| `src/main/resources/static/js/admin/banners.js` | 357 | 轮播图管理主脚本 |
| `src/main/resources/static/js/admin/banner.js` | 233 | 轮播图管理旧版/补充脚本 |

## 13. 测试代码

| 文件 | 行数 | 作用 |
| --- | ---: | --- |
| `src/test/java/com/gamestore/GameCommunityStoreApplicationTests.java` | 13 | Spring Boot 上下文加载测试 |
| `src/test/java/com/gamestore/service/GameServiceTest.java` | 130 | `GameService` 业务逻辑测试 |

## 14. 备注

- 从代码量看，当前项目最重的 3 个业务服务是：`InnovationService`、`StoreService`、`GameService`。
- 从页面体量看，`templates/index.html`、`templates/game-detail.html`、`static/post-detail.html` 是前端交互最密集的 3 个页面。
- 这份清单更适合作为论文“系统实现”章节、代码答辩说明、开发交接文档的索引页使用。
