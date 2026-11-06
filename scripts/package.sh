#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."

mvn -q clean package -DskipTests
ls -lh target/*.jar
echo "[package] 打包完成；删除过类文件后必须使用 clean"
