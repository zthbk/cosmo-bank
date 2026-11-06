FROM nginx:alpine

ARG TZ=Asia/Shanghai
ENV TZ=${TZ}

# 新前端尚未创建：先提供空静态目录，避免 COPY ./web 导致构建失败。
# 新前端创建后，在此恢复 Node 构建阶段，并将产物复制到 /opt/dist。
RUN mkdir -p /opt/dist

CMD ["nginx", "-g", "daemon off;"]
