#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

PROFILES="${1:-dev,local}"

if [ -z "${WVP_ADMIN_PASSWORD:-}" ]; then
    echo "[run] 未设置 WVP_ADMIN_PASSWORD；仅首次初始化管理员密码时需要"
fi

echo "[run] profile=${PROFILES}"
mvn spring-boot:run -Dspring-boot.run.profiles="${PROFILES}"
