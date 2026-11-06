<!-- 编译 -->

# 编译

## 1. 环境要求

表 1 给出编译与运行所需环境。

| 依赖 | 版本 | 用途 | 是否必须 |
|---|---|---|---|
| JDK | 17 | 编译与运行 | 是 |
| Maven | 3.9+ | 管理 Java 依赖 | 是 |
| MySQL | 8.4 | 配置与历史数据 | 是 |
| Redis | 兼容版本 | 设备、会话、SIP 事务 | 是 |
| ZLMediaKit | 可用版本 | 媒体收流与分发 | 是 |
| Docker | 可选 | 部署 MySQL/Redis/ZLM | 否 |

- 当前没有前端工程，不需要 Node/npm，也不要执行旧前端的 `npm run build:prod`。
- 不要使用 JDK 21 或更高版本构建。

## 2. 编译命令

```bash
# 日常最小检查
mvn -q -DskipTests compile

# 测试编译，改 DAO/Service/测试后必须执行
mvn -q -DskipTests test-compile

# 打包
mvn -q -DskipTests package

# 删除过类文件后必须 clean，避免 target 旧 class 残留
mvn -q clean package -DskipTests
```

## 3. 说明

- `pom.xml` 硬编码 `<skipTests>true</skipTests>`；`-DskipTests=false` 不会执行测试。跑测试用 IDE，或临时改为 `false` 后恢复。
- 当前没有 CI workflow，所有验证在本地完成。
- 新前端创建后，构建命令统一为 `npm run build`，输出 `src/main/resources/static`。
- 编译完成后进入配置与部署：`config.md`、`deployment.md`。
