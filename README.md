# 岸端企业平台（GB28181 视频底座）

> 基于 WVP-Pro 裁剪后的船舶智能监控岸端视频底座 | Java 17 + Spring Boot 3.4.4 | 当前无前端工程

本项目以 WVP-Pro 为视频与设备底座，保留 GB28181 设备接入、通道管理、实时点播、历史回放、对讲/广播、PTZ、ZLM 媒体节点、云录像、录像计划、设备告警、移动位置、用户权限与日志能力，面向船舶智能监控 2.0 岸端平台继续建设。

当前仓库是**清理后的视频与设备底座**，不是完整的岸端企业平台。AI 告警、告警处置、统计报表、值班室播报、对象存储、全国平台对接和新 Vue 3 前端尚未交付。

## 1. 当前能力与边界

| 能力 | 状态 | 说明 |
|---|---|---|
| GB28181 设备、通道、目录、心跳 | 保留 | SIP 信令、设备/通道管理、Catalog |
| 实时点播、历史回放、对讲/广播、PTZ | 保留 | 依赖 ZLMediaKit |
| ZLM 媒体节点、收流、录像 | 保留 | 仅保留 ZLM，ABL 已删除 |
| 云录像、录像计划、录像下载 | 保留 | `/api/cloud/record`、`/api/record/plan`、`/api/gb_record` |
| 设备告警、移动位置 | 保留 | 作为设备侧来源，不等同于 AI 告警业务 |
| 用户、角色、API Key、日志 | 保留 | 登录、权限、审计基础 |
| GB28181 平台级联 | 冻结 | 代码暂存，C3 删除前不对外承诺 |
| JT1078/JT808、FTP | 已删除 | 终端不含 JT808，定位走 HTTPS |
| 地图与矢量瓦片 | 已删除 | 无电子地图需求 |
| ABL 媒体服务 | 已删除 | 媒体节点全部为 ZLM |
| 流代理、推流、`/api/sy` | 已删除 | 无拉流源、无推流源、无第三方调用 |
| 非 MySQL 数据库 | 已删除 | 仅支持 MySQL 8.4 |
| 旧 Vue 2 前端 | 已删除 | 新 Vue 3 + Vite 6 前端待建 |

## 2. 技术栈

- 后端：Java 17、Spring Boot 3.4.4、Maven 单模块。
- Web/安全：Spring MVC、Spring Security、WebSocket、springdoc-openapi、Knife4j。
- 数据：MyBatis 3.0.4、PageHelper 2.1.1、MySQL 8.4（`mysql-connector-j` 8.4.0）、Redis。
- 协议与媒体：Jain SIP 1.3.0-91、GB28181、OkHttp、ZLMediaKit 外部媒体服务。
- 工具：Lombok、fastjson2、Caffeine、Guava、Hutool。
- 前端：当前无 `web/` 工程；目标为 Vue 3 + Vite 6 + Element Plus 2.13，另行创建。

## 3. 目录结构

```text
src/main/java/com/genersoft/iot/vmp/
├── gb28181/        SIP 信令、设备/通道、点播、回放、级联、PTZ、位置
├── media/          ZLMediaKit 接入、媒体 Bean/Event
├── service/        跨模块服务、录像、告警、集群 RPC
├── storager/       Redis 缓存与 DAO
├── vmanager/       管理 API（用户、告警、云录像、录像计划、服务控制）
├── web/gb28181/    外部兼容 API（/api/v1）
├── conf/           配置、安全、调度、Redis
└── utils/          时间、媒体、通用工具
src/main/resources/ 配置、日志、SQL 映射
docker/             Docker Compose、MySQL/Redis/ZLM/WVP/Nginx
sql/                 按版本维护的 MySQL 脚本
doc/                编程规范与站点
文档/               改造方案、清理计划、C3 清单、前端契约
```

入口类：`src/main/java/com/genersoft/iot/vmp/VManageBootstrap.java`。

## 4. 快速开始

### 4.1 环境要求

- JDK 17（不要使用 JDK 21 或更高版本构建）。
- Maven 3.9+。
- MySQL 8.4。
- Redis。
- ZLMediaKit（ZLM）。
- Docker 可选；全栈部署推荐 Docker Compose。

当前没有前端工程，不需要 Node/npm。

### 4.2 初始化数据库

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS wvp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
mysql -uroot -p wvp < sql/3.0.0/初始化-mysql-3.0.0.sql
```

数据库脚本按版本维护，无 Flyway/Liquibase；生产升级按 `sql/<version>/更新-*.sql` 顺序执行。

### 4.3 配置

- `src/main/resources/application.yml` 默认激活 `dev`。
- `application-dev.yml`、`application-local.yml` 等私有 profile 被 gitignore，不提交。
- `src/main/resources/配置详情.yml` 是配置项示例，不能直接用于启动。
- 至少配置：
  - `spring.datasource.url/username/password`：MySQL。
  - `spring.data.redis.host/port/database`：Redis。
  - `sip.id/domain/password/port`：GB28181。
  - `media.ip/http-port/secret`：ZLM 地址与 API secret。
  - `user-settings.*`：点播超时、录像、按需拉流等。
- 密码、ZLM secret、设备口令只放本地未跟踪配置或环境变量，不写入 README、文档、提交信息或接口。

### 4.4 本地启动（IDE / Maven）

本地开发可以使用本机 MySQL，Redis 与 ZLM 用 Docker 启动；也可以全部使用 Docker Compose。

```bash
# 编译
mvn -q -DskipTests compile

# 从源码启动，激活 dev；使用本地私有配置时追加 local
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

默认端口：

- WVP HTTP：`18080`。
- SIP：`8116`。
- ZLM HTTP：`9092`（本地配置示例；以实际配置为准）。

首次启动需要设置管理员密码：

```bash
WVP_ADMIN_PASSWORD='满足复杂度要求的管理员密码' \
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

`AdminPasswordInitializer` 只在管理员仍为默认密码时设置，已改密码不会覆盖。

### 4.5 Docker Compose 全栈

```bash
cd docker
docker compose up -d
```

- 启动 MySQL 8.4、Redis、ZLM、WVP、Nginx。
- 默认通过 `8080` 访问 Nginx，SIP 端口默认 `8116`；端口以 `docker/.env` 为准。
- 复制 `docker/.env.example` 为 `docker/.env` 后填写必填项；`.env` 不再跟踪，不要提交真实口令。
- 停止：`docker compose down`。

## 5. 访问与接口

- 管理端页面：当前无前端工程，接口可直接通过 Knife4j/OpenAPI 调试。
- API 文档：`http://127.0.0.1:18080/doc.html`。
- OpenAPI：`/v3/api-docs`。
- 主要接口分组：
  - `/api/common/channel`：公共通道、分组、区域、点播、对讲、广播。
  - `/api/play`、`/api/playback`、`/api/front-end`：播放、回放、PTZ。
  - `/api/gb_record`：国标录像查询与下载。
  - `/api/cloud/record`、`/api/record/plan`：云录像与录像计划。
  - `/api/alarm`：设备告警。
  - `/api/position`：移动位置。
  - `/api/user`、`/api/role`、`/api/userApiKey`：用户、角色、API Key。
  - `/api/server`、`/api/log`：服务状态与日志。
  - `/api/v1/*`：外部兼容 API，响应结构不要随意修改。
- `/api/platform/*` 平台级联接口仍存在但已冻结，不作为当前交付能力。

## 6. 构建与测试

```bash
mvn -q -DskipTests compile
mvn -q -DskipTests test-compile
mvn -q -DskipTests package
mvn -q clean package -DskipTests
```

- 删除过类文件后必须使用 `mvn clean package`，否则 `target/classes` 中的旧 class 可能被打进 jar。
- `pom.xml` 硬编码 `<skipTests>true</skipTests>`，`-DskipTests=false` 不会执行测试；需要跑测试时用 IDE，或临时改为 `false` 后恢复。
- 测试编译仍会执行，改动 DAO/Service/测试后必须跑 `test-compile`。
- 当前没有 CI workflow，所有验证在本地完成。

## 7. 当前清理状态与已知限制

- 已完成：B4（非 MySQL 支持）、B1（JT1078/FTP）、B3（地图/瓦片）、C4（ABL）、C1（流代理）、C2（推流与 `/api/sy`）。
- 进行中：C3 GB28181 平台级联，处于冻结观察期；未完成 SDP 地址重构前不要删除 `Platform` 类。
- 旧 `web/` 已删除，新前端待建；不要执行 `cd web`、`npm install` 或旧前端构建命令。
- 已删除模块的数据库表和通道数据可能仍存在（如 `wvp_jt_*`、`wvp_stream_proxy`、`wvp_stream_push`、`data_type=2/3`），但代码已不依赖，清理需单独备份、停写、观察。
- `doc/编程规范.md` 与上游 WVP 文档中仍可能残留 JT1078、流代理、推流、多数据库等旧描述；以当前代码和清理计划为准，不要据此恢复已删除模块。
- `application-*.yml` 不会打进 jar；`java -jar` 从仓库根目录启动会缺少 DataSource 配置。请从源码/IDE 启动，或显式提供外部配置。
- 本地 `target/`、`logs/`、`config/`、`打包/`、Docker 数据卷、私有 profile 与本地 compose 覆盖文件均不应提交。

## 8. 文档索引

| 文档 | 用途 |
|---|---|
| `doc/编程规范.md` | 分层、Controller、事务、DAO、日志、并发、测试规范（部分内容仍描述已删除模块） |
| `文档/冗余代码与文件清理计划.local.md` | 清理批次、执行记录、验收与回滚（本地计划） |
| `文档/C3平台级联方法级清单.local.md` | C3 逐方法删除矩阵（本地清单） |
| `文档/岸端企业平台改造方案.local.md` | 岸端平台目标架构与 P1 能力 |
| `文档/船舶终端与岸端企业平台通信设计说明.local.md` | 终端 HTTPS/Kafka/REST 对接口径 |
| `文档/前端重建参考.local.md`、`前端重建参考-API契约.local.md` | 新前端页面与 API 契约 |
| `docker/docker-compose.yml`、`docker/media/config.ini` | 部署编排与 ZLM 配置 |
| `sql/3.0.0/初始化-mysql-3.0.0.sql` | MySQL 初始化脚本 |

## 9. 提交约定

- 提交信息使用中文，简短明确；一个清理批次或一个功能点一个提交。
- 未经明确要求不推送远程、不重写历史。
- 不提交 `target/`、`logs/`、`config/`、`打包/`、Docker 数据卷、私有 `application-*.yml`、本地 compose 覆盖文件、真实密码/密钥。
- 提交前至少执行 `compile`、`test-compile`、`git diff --check`；涉及删除类时执行 `mvn clean package`。
