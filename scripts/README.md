# 常用脚本

> 适用于 Git Bash / Linux；脚本会自动切换到仓库根目录。

表 1 给出脚本用途。

| 脚本 | 用途 | 说明 |
|---|---|---|
| `build.sh` | 编译 | `mvn -q -DskipTests compile` |
| `check.sh` | 校验 | 编译、测试编译、`git diff --check`、已删模块零引用扫描 |
| `package.sh` | 打包 | `mvn -q clean package -DskipTests` |
| `run.sh` | 本地运行 | 默认 `dev,local`，可传入 profile |

用法：

```bash
./scripts/build.sh
./scripts/check.sh
./scripts/package.sh
./scripts/run.sh                 # 默认 dev,local
./scripts/run.sh dev             # 指定 profile
```

注意：

- `run.sh` 不读取、不打印管理员密码；首次启动如需初始化，先在当前 shell 设置 `WVP_ADMIN_PASSWORD`。
- `package.sh` 使用 `clean`，避免 `target` 旧 class 残留。
- `check.sh` 的零引用扫描只覆盖已删除模块关键词；`Platform` 处于 C3 冻结期，不在扫描范围。
- Docker 全栈构建与启动仍使用 `docker/docker-compose.yml`，不要与本地运行同时占用 8116。
