<!-- 配置 -->

# 配置

## 1. 配置文件

- `src/main/resources/application.yml` 默认激活 `dev`。
- `application-dev.yml`、`application-local.yml` 等私有 profile 被 gitignore，不提交。
- `src/main/resources/配置详情.yml` 是配置项示例，不能直接用于启动。
- 至少配置：数据源、Redis、SIP、媒体节点、`user-settings`。

## 2. 数据库

- 仅支持 MySQL 8.4；不新增 PostgreSQL、金仓、达梦、H2 分支。
- 数据库名 `wvp`，字符集 `utf8mb4`。
- 初始化脚本：`数据库/3.0.0/初始化-mysql-3.0.0.sql`。
- 升级脚本按 `数据库/<version>/` 顺序执行；无 Flyway/Liquibase。

表 1 给出数据源必填项。

| 配置项 | 说明 |
|---|---|
| `spring.datasource.url` | MySQL 连接地址 |
| `spring.datasource.username` | 数据库用户 |
| `spring.datasource.password` | 数据库口令，只放本地未跟踪配置 |

## 3. Redis

- 配置 `spring.data.redis.host/port/database`。
- 设备状态、InviteInfo、SIP 事务、集群消息均依赖 Redis。
- 建议 WVP 单独使用一个 db。

## 4. SIP 与媒体节点

表 2 给出 SIP 与 ZLM 必填项。

| 配置项 | 说明 |
|---|---|
| `sip.port` | SIP 监听端口，默认 8116 |
| `sip.domain` | 国标域，通常取 ID 前十位 |
| `sip.id` | SIP 服务 ID |
| `sip.password` | 设备公共认证口令，只放本地配置 |
| `media.ip` | ZLM 地址 |
| `media.http-port` | ZLM HTTP 端口，默认 9092 |
| `media.secret` | ZLM API secret，只放本地配置 |
| `media.rtp.enable` | 是否启用多端口模式 |

- ZLM 与 WVP 高频通信，部署时保持网络稳定。
- ZLM 使用 Docker 时保持端口映射一致，避免 SDP 地址错误。

## 5. user-settings

- `play-timeout`：点播/回放等待超时，单位毫秒。
- `record-sip`：国标点播是否录像。
- `stream-on-demand`：按需点播，无人观看时释放媒体流。
- `sip-log`：SIP 日志开关；观察期可开启，日志含设备编码/IP，需控制访问。

## 6. 敏感配置

- 密码、ZLM secret、设备口令、Token 不提交、不写文档、不打印日志。
- 真实口令只放本地未跟踪配置或环境变量。
