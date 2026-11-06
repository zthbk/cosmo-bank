FROM node:20-alpine AS builder

WORKDIR /app
COPY web/package.json web/package-lock.json ./
RUN npm ci --no-audit --no-fund

COPY web/ ./
RUN npm run build -- --outDir dist

FROM nginx:alpine

ARG TZ=Asia/Shanghai
ENV TZ=${TZ}

COPY --from=builder /app/dist /opt/dist

CMD ["nginx", "-g", "daemon off;"]
