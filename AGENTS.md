# AGENTS.md

## 项目概览

- 技术栈：Java 17 + Spring Boot 3.4.4 单 Maven 模块；Spring Security、MyBatis、PageHelper、Redis、MySQL 8.4、Jain SIP、OkHttp、ZLMediaKit；前端 `web/` 使用 Vue 3.5 + Vite 6.4 + Element Plus 2.13 + Pinia。
- 核心依赖：`spring-boot-starter-web/security/data-redis`、`mybatis-spring-boot-starter`、`mysql-connector-j` 8.4、`jain-sip-ri`、`okhttp`、`springdoc/knife4j`、`fastjson2`、`caffeine`、`lombok`。
- 部署方式：`docker/docker-compose.yml` 编排 MySQL 8.4、Redis、ZLM、WVP、Nginx；本地开发可只用 Docker 起 Redis/ZLM，WVP 从 IDE 或 Maven 启动。
- 入口：`src/main/java/com/genersoft/iot/vmp/VManageBootstrap.java`；后端代码根目录：`src/main/java/com/genersoft/iot/vmp/`。
- 当前状态：旧 `web/` 已删除，新 Vue 3 前端 `web/` 已创建（M1 登录与全局框架、M2 动态监控与告警列表）；`biz/` 已建 5 类档案 CRUD 与告警接入/查询；JT1078/FTP、地图/瓦片、ABL、流代理、推流与 `/api/sy`、非 MySQL 数据库支持已删除；C3 平台级联处于冻结观察期。

## 常用命令

```bash
# 编译（日常最小检查）
mvn -q -DskipTests compile

# 测试编译（改 DAO/Service/测试后必须执行）
mvn -q -DskipTests test-compile

# 打包；删除过类文件时必须先 clean，避免旧 class 残留进 jar
mvn -q -DskipTests package
mvn -q clean package -DskipTests

# 本地启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# 使用本机 MySQL 和本地 Redis/ZLM 配置时
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local

# 全栈 Docker
cd docker && docker compose up -d

# 前端（Node 20 LTS，工程在 web/）
cd web && npm ci && npm run dev     # 开发，代理 /api、/biz 到 18080
cd web && npm run build             # 构建到 src/main/resources/static
```

- 运行单测：`pom.xml` 硬编码 `<skipTests>true</skipTests>`，`-DskipTests=false` 无效；用 IDE，或临时改为 `false` 后执行并恢复。
- 代码检查：本项目没有 Checkstyle/Spotless；以 `compile`、`test-compile`、`package` 和 `git diff --check` 为准。
- 前端：工程在 `web/`，依赖安装用 `npm ci`，构建用 `npm run build`；构建产物输出到 `src/main/resources/static`（已 gitignore），不要手工提交。
- 本地默认端口：HTTP 18080、SIP 8116；ZLM HTTP 9092，媒体端口以 `docker/.env` 和实际配置为准。

## 架构约束

- **只支持 MySQL 8.4。** 不要新增 PostgreSQL、Kingbase、达梦、H2 的驱动、SQL 脚本、方言分支或 `databaseId` 分支。
- **表名前缀：** 视频底座表用 `wvp_`，岸端业务表用 `biz_`；业务表脚本放 `sql/3.1.0/`、`sql/3.2.0/` 等版本目录，不修改已发布脚本。
- **运行态分三层：** MySQL（配置/历史）、Redis（设备、InviteInfo、SIP 事务、集群消息）、JVM（等待回调、DynamicTask）。修改点播/会话逻辑必须同时考虑三层及清理路径。
- **SIP 事务以 Redis 为权威。** `SipInviteSessionManager` 的事务重启后仍可能存在；设备 BYE/事务清理优先复用 `DialogTerminationService`，不要新开直接 `cmder.streamByeCmd` 的旁路。
- **DAO 参数必须绑定。** 动态 SQL 放 `dao/provider`，外部参数一律 `#{}`；`${}` 只能用于白名单排序字段或内部常量，禁止拼接用户输入；不新增 XML Mapper。
- **已删除模块不得恢复。** 不要重新引入 `jt1078/`、`conf/ftpServer/`、`media/abl/`、`streamProxy/`、`streamPush/`、`web/custom/`（`/api/sy`）、地图/矢量瓦片或 `MediaStreamUtil.isKeywords`。C3 平台级联未完成前，不要按旧文档恢复平台分支。
- **Controller 保持薄。** 参数校验和响应组织放 Controller，业务与 SQL/Redis 放 Service/DAO；普通返回由 `GlobalResponseAdvice` 包装为 `WVPResult`，业务错误抛 `ControllerException`。

## 编码规范

- 使用 JDK 17 语法和 API；不要使用虚拟线程、JDK 21+ 专属接口或依赖更高版本运行时的库。
- 修改前先读目标文件和相关调用链；不做无关格式化、重排、顺手重构。删除类/模块时同步清理测试、配置、常量和引用。
- 区分 `deviceId`/`channelId`（国标编码，通常 `String`）与 `id`/`channelDbId`/`dataDeviceId`（数据库主键，通常 `Integer`）；不要混用参数名。
- 时间统一走 `utils/DateUtil`，时区固定 `Asia/Shanghai`；不要新增 `SimpleDateFormat`，不要恢复已删除的 JT1078 时间格式方法。
- 不使用 `Optional`；列表/Map 返回空集合而不是 `null`；判空优先 Spring `ObjectUtils`/`StringUtils`，注意不要与 Apache Commons 同名类混用。
- `@Transactional` 只放在 `ServiceImpl` 的 public 方法；事务只包数据库写操作，不要把 SIP 等待、ZLM HTTP、Redis 轮询放进去；同类内部调用会绕过代理。
- `PageHelper.startPage(page, count)` 后必须紧接查询，中间不要插入其他 SQL。
- 日志用 `@Slf4j` + 参数化占位符，中文、带模块前缀；禁止 `System.out`、`printStackTrace`、字符串拼接日志；禁止打印密码、Token、Authorization、Redis 密码、设备口令、pushKey 或完整敏感实体。
- 固定取值用 `ErrorCode`、`InviteErrorCode`、`InviteSessionType` 等常量/枚举，不要在 Java 代码散落魔法值。
- 修 bug 补回归测试；测试用 JUnit 5 + Mockito，不启动 Spring 上下文，不依赖 MySQL/Redis/ZLM；方法名用英文 `method_condition_expected`。

## 文档索引

| 文档 | 用途 | 注意 |
|---|---|---|
| `docs/guide/编程规范.md` | 分层、Controller、事务、DAO、日志、并发、测试规范 | 已按当前基线重写 |
| `docs/local/后端运行参考.local.md` | 能力边界、编译、配置、启动与端口 | 整理自原 `doc/` |
| `docs/local/WVP功能参考.local.md` | 设备、通道、云录像、对讲、节点管理 | 整理自原 `doc/` |
| `docs/local/GB28181流程与排障参考.local.md` | 注册、点播、编码规则、抓包与常见错误 | 整理自原 `doc/` |
| `docs/local/岸端企业平台前端还原规格.local.md` | 前端页面、视觉、接口、任务书与指南核对 | 前端开发与验收基线 |
| `docs/local/doc目录迁移说明.local.md` | 原 `doc/` 目录逐篇处置与引用更新 | 迁移记录 |
| `docs/local/冗余代码与文件清理计划.local.md` | 清理批次、执行记录、验收与回滚 | 本地计划，不随代码提交 |
| `docs/local/C3平台级联方法级清单.local.md` | C3 逐方法删除矩阵 | C3 执行前必须逐项复核 |
| `docs/local/C3冻结观察记录.local.md` | C3 观察期记录与解除门槛 | 本地记录，不随代码提交 |
| `docs/local/岸端企业平台与全国平台对接设计说明.local.md` | REST/Kafka/OAuth2.0 对接设计 | 本地设计文档 |
| `docs/local/岸端企业平台改造方案.local.md` | 岸端平台目标架构与 P1 能力 | 本地设计文档 |
| `docs/local/船舶终端与岸端企业平台通信设计说明.local.md` | 终端 HTTPS/Kafka/REST 对接口径 | 本地设计文档 |
| `docs/local/前端重建参考.local.md`、`前端重建参考-API契约.local.md` | 新前端页面与 API 契约 | 旧 `web/` 已删除 |
| `README.md` | 当前项目说明、启动、部署与清理状态 | 与本文件同步维护 |
| `docker/docker-compose.yml`、`docker/media/config.ini` | 部署编排与 ZLM 配置 | 本地覆盖文件不要提交 |
| `sql/3.0.0/初始化-mysql-3.0.0.sql` | MySQL 初始化脚本 | 无 Flyway/Liquibase，按版本手工维护 |

## Git 工作流

- 当前主分支 `master`；未经用户明确要求，不推送远程、不重写历史。
- 提交信息使用中文，简短明确（建议不超过 20 字）；一个清理批次一个提交，不混合无关改动。
- 提交前至少执行：`mvn -q -DskipTests compile`、`mvn -q -DskipTests test-compile`；涉及删除类时用 `mvn -q clean package -DskipTests`；再执行 `git diff --check` 和相关引用检索。
- 不提交：`target/`、`logs/`、`config/`、`打包/`、`docker/volumes/`、本地 `application-*.yml`、本地 compose 覆盖文件、ZLM 开发配置、真实口令/密钥。
- `docker/.env` 不再跟踪；从 `docker/.env.example` 复制后本地填写，禁止提交真实口令。
- PR/评审先给结论：建议合并 / 可合并但需处理 / 建议发回作者修改；再说明 PR 目的、描述是否成立、合并后待办。

## 已知陷阱

- **`mvn package` 不清理旧 class。** 删除类后直接 package 可能把 `target/classes` 中的旧类打进 jar（曾出现已删除的 `ftpServer` 类）；删除类后必须 `mvn clean package`。
- **`application-*.yml` 不打进 jar。** `maven-jar-plugin` 排除 `application.yml`、`application-*.yml`，`maven-resources-plugin` 只复制到 `target/`；`java -jar` 从仓库根目录启动会缺少 DataSource 配置。用源码/IDE 启动，或显式指定外部配置目录。
- **`pom.xml` 硬编码 `skipTests=true`。** `-DskipTests=false` 不会执行测试；`test-compile` 仍会编译测试。需要真正跑测试时改 IDE 配置或临时改 pom 后恢复。
- **本地配置默认不完整。** `application.yml` 激活 `dev`，`application-dev.yml`/`application-local.yml` 被 gitignore；新环境需自行创建本地 profile，MySQL 密码只放未跟踪配置，不提交。
- **`docker/.env` 需本地创建。** 先复制 `docker/.env.example` 为 `docker/.env`；直接 `docker compose` 会因必填值为空而插值失败，真实口令只填本地 `.env`。
- **管理员初始密码。** 初始化 SQL 不保留可用默认口令；首次启动需设置 `WVP_ADMIN_PASSWORD`（满足复杂度），否则 `AdminPasswordInitializer` 只告警，登录密码仍不可用。
- **C3 不能直接删 `Platform`。** 必须先把 `SIPRequestProcessorParent` 的 SDP 应答改为设备 IP/端口/设备 ID 重载，去掉 `InviteRequestProcessor` 中的 `new Platform()` 地址载体，再处理 `SsrcTransaction.platformId`、`SendRtpInfo.sendToPlatform`、`AudioBroadcastCatch.isFromPlatform`。
- **上游 WVP 文档站已删除。** 原 `doc/` 的保留内容已整理到 `docs/local/`，`docs/guide/编程规范.md` 已按当前基线重写；不要再按上游旧文档恢复 JT1078、流代理、推流、旧 Vue 2 前端或多数据库。
- **已删模块的表可能仍在。** `wvp_jt_terminal`、`wvp_jt_channel`、`wvp_stream_proxy`、`wvp_stream_push` 等可能尚未清理，但代码已不依赖；不要新增对这些表的查询。
- **`GlobalResponseAdvice` 有路径例外。** `/api/v1`、`/api/video-`、`/index/hook` 等不自动包装；新增兼容接口时确认响应结构，避免重复包 `WVPResult`。
- **`DynamicTask` key 全局唯一。** 完成、取消、超时后必须移除 future，否则同 key 无法重新注册；`DeferredResult` 只能成功一次，清理按本次请求拥有的状态做条件删除。
- **当前没有 CI workflow。** `.github/` 已删除，不要假设推送后自动编译；所有验证在本地完成。
- **前端构建产物不提交。** `web/node_modules/`、`web/dist/`、`src/main/resources/static/` 均已忽略；`npm run build` 会写入 `src/main/resources/static`，由后端或 nginx 提供。
