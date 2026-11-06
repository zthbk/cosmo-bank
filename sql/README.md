# SQL 脚本

> 仅支持 MySQL 8.4；按版本目录维护，无 Flyway/Liquibase。

表 1 给出当前脚本。

| 文件 | 用途 |
|---|---|
| `3.0.0/初始化-mysql-3.0.0.sql` | 全新部署初始化 |
| `3.0.0/更新-mysql-3.0.0.sql` | 3.0.0 版本内更新 |

说明：

- `docker/docker-compose.yml` 挂载 `sql/3.0.0/初始化-mysql-3.0.0.sql` 作为 MySQL 首次初始化脚本。
- 旧版本（2.6.9 至 2.7.4）脚本已移到仓库内 `backup/sql-old/`，该目录被 gitignore，不提交；需要回滚时从本地备份取用。
- 数据库结构变更新增版本目录，不修改已发布版本脚本。
