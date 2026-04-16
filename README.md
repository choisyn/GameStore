# Game Community Store

一个基于 Spring Boot 的游戏社区商城系统，采用单体 Web 架构，集成了游戏商城、用户中心、购物车与订单、游戏库、积分、社区讨论和后台管理等能力，适合本地演示与毕业设计场景使用。

## 项目说明

- 技术栈：Spring Boot 3.2.0、Spring Data JPA、Thymeleaf、MySQL 8、Bootstrap 5、Docker Compose
- 项目形态：前台商城、用户登录、社区、后台管理共用一套后端服务
- 默认访问地址：`http://localhost:8080`
- 健康检查地址：`http://localhost:8080/api/health`
- 默认数据库：`bishe`

## 部署过程

### 1. 准备环境

确保本机已安装并启动 Docker Desktop，且当前目录为项目根目录。

### 2. 初始化数据库

当前应用默认连接 `localhost:3307` 上的 MySQL。若本机还没有演示数据库，可执行下面的命令初始化一个 MySQL 容器：

```bash
docker run -d \
  --name bishe-mysql-3307 \
  -p 3307:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_DATABASE=bishe \
  -e MYSQL_USER=gamestore \
  -e MYSQL_PASSWORD=password \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -v bishe_mysql_data_3307:/var/lib/mysql \
  -v "$(pwd)/开发文档/sql/bishe_full_notablespaces_20260416_083830.sql:/docker-entrypoint-initdb.d/01_bishe_full.sql:ro" \
  mysql:8.0 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci
```

说明：数据库数据保存在 Docker volume `bishe_mysql_data_3307` 中，普通重启容器不会清空数据。

### 3. 部署应用

首次启动或普通启动：

```bash
docker compose up -d --build app
```

项目更新后重新部署：

```bash
docker compose up -d --build --force-recreate app
```

说明：`Dockerfile` 会在镜像构建时自动执行 Maven 打包，因此不需要额外手动执行 `mvn package`。

### 4. 验证运行

```bash
docker compose ps
curl -i http://127.0.0.1:8080/api/health
```

浏览器访问：

- `http://localhost:8080/`
- `http://localhost:8080/login`
- `http://localhost:8080/admin`
