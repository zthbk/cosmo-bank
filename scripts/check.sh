#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

mvn -q -DskipTests compile
mvn -q -DskipTests test-compile
git diff --check HEAD -- . ':(exclude)docker/.env' ':(exclude)docker/media/config.ini'

if grep -rniE "jt1078|streamProxy|streamPush|media\.abl|web\.custom|/api/sy" src/main/java src/test/java; then
    echo "[check] 发现已删除模块引用，请清理后再提交" >&2
    exit 1
fi

echo "[check] 编译、测试编译、空白检查与零引用检查通过"
