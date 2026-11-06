可以在当前目录下：
使用`docker compose up -d`直接运行。
使用`docker compose up -d --build --force-recreate`强制重新构建所有服务的镜像并删除旧容器重新运行

`.env`用来配置环境变量，在这里配好之后，其它的配置会自动联动的。

当前没有前端工程，`polaris-nginx` 镜像仅提供反向代理；新前端创建后，在 `docker/nginx/Dockerfile` 恢复 Node 构建阶段，并将产物复制到 `/opt/dist`。

其它的文件的作用暂不明确