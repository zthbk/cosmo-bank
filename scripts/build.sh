#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

mvn -q -DskipTests compile
echo "[build] 编译完成"
