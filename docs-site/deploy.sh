#!/bin/bash

set -e

# 配置变量
IMAGE_NAME="ghcr.io/begoniahe/shimeji-live-docs"
VERSION="latest"
CONTAINER_NAME="shimeji-docs"

echo "🚀 开始部署 Shimeji-Live 文档站点..."

# 停止并删除现有容器
echo "📦 停止现有容器..."
docker stop $CONTAINER_NAME || true
docker rm $CONTAINER_NAME || true

# 拉取最新镜像
echo "📥 拉取最新镜像..."
docker pull $IMAGE_NAME:$VERSION

# 启动服务
echo "🔄 启动服务..."
docker-compose -f docker-compose.prod.yml up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 10

# 健康检查
echo "🔍 执行健康检查..."
if curl -f http://localhost:80 > /dev/null 2>&1; then
    echo "✅ 部署成功！服务正在运行"
else
    echo "❌ 部署失败！服务无法访问"
    exit 1
fi

# 清理未使用的镜像
echo "🧹 清理未使用的镜像..."
docker image prune -f

echo "🎉 部署完成！"
