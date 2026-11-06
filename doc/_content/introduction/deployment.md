<!-- 部署 -->

# 部署

## 1. 本地启动

```bash
# 使用本地 MySQL、Redis、ZLM
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

- 默认 HTTP 18080、SIP 8116；ZLM HTTP 9092（以本地配置为准）。
- 首次启动通过 `WVP_ADMIN_PASSWORD` 设置管理员初始密码；已修改过密码则忽略该变量。
- 管理员密码提示见本地凭据文件，不写入文档。

## 2. Docker Compose

```bash
cd docker
docker compose up -d
```

- 编排 MySQL 8.4、Redis、ZLM、WVP、nginx。
- 复制 `docker/.env.example` 为 `docker/.env` 后填写必填项；`.env` 不提交，真实口令只放本地。
- 当前没有前端工程，nginx 仅提供反向代理；新前端创建后恢复 `docker/nginx/Dockerfile` 的构建阶段。

## 3. 端口

表 1 给出主要端口，实际以配置与 `docker/.env` 为准。

| 服务 | 端口 | 类型 | 说明 |
|---|---|---|---|
| WVP HTTP | 18080（本地）/ 18978（Docker 内部） | TCP | 管理接口与文档 |
| SIP | 8116 | UDP/TCP | GB28181 信令 |
| ZLM HTTP | 9092 | TCP | RESTful 与 Hook |
| ZLM 媒体 | 按 `media.rtp` 配置 | UDP/TCP | 收流与推流 |

- 生产环境按需开放端口，修改默认端口，避免直接暴露 SIP 到公网。
- ZLM 与 WVP 不要分属两个隔离网络。

## 4. 当前限制

- 平台级联冻结，不作为交付能力。
- 新前端待建；不要执行 `cd web` 或 npm 命令。
- `application-*.yml` 不打进 jar；`java -jar` 需显式提供外部配置目录。
- 删除类后必须 `mvn clean package`，避免 `target` 旧 class 残留。
