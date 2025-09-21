# Shimeji-Live 文档站点

这是 Shimeji-Live 项目的官方文档站点，使用 VitePress 构建。

## 本地开发

```bash
# 安装依赖
cd docs-site
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

## Docker 部署

### 本地开发环境

```bash
# 构建镜像
docker build -t shimeji-live-docs .

# 运行容器
docker run -p 80:80 shimeji-live-docs

# 使用 docker-compose
docker-compose up -d
```

### 生产环境部署

#### 自动化构建和推送

本项目已配置 GitHub Actions 自动化工作流程。当您提交代码到 `main` 分支或修改 `docs-site/` 目录下的文件时，会自动：

1. 构建 Docker 镜像
2. 推送到 GitHub Container Registry (`ghcr.io/begoniahe/shimeji-live-docs`)
3. 自动生成版本标签

**镜像标签说明：**

- `latest` - 主分支最新版本
- `main` - 主分支版本
- `sha-<commit>` - 特定提交版本
- `pr-<number>` - Pull Request 版本

#### 部署到生产服务器

#### 手动构建（可选）

如果需要在本地测试或手动构建：

```bash
# 构建镜像
docker build -t shimeji-live-docs .

# 运行测试
docker run -p 3000:80 shimeji-live-docs
```

#### 生产环境配置文件

创建 `docker-compose.prod.yml`：

```yaml
version: '3.8'

services:
  docs:
    image: ghcr.io/begoniahe/shimeji-live-docs:latest
    container_name: shimeji-docs
    restart: unless-stopped
    ports:
      - "3000:80"
    volumes:
      - ./logs:/var/log/nginx
    environment:
      - NODE_ENV=production
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost/"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  nginx:
    image: nginx:alpine
    container_name: shimeji-nginx
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
      - ./ssl:/etc/nginx/ssl
      - ./logs:/var/log/nginx
    depends_on:
      - docs
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost/"]
      interval: 30s
      timeout: 10s
      retries: 3
```

创建 `nginx.conf`：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 重定向到 HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    # SSL 配置
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;

    # 安全头
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Strict-Transport-Security "max-age=63072000; includeSubDomains; preload";

    # 静态文件缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    location / {
        proxy_pass http://docs:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket 支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

#### 部署脚本

创建 `deploy.sh`：

```bash
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
```

#### 监控和日志

```bash
# 查看容器状态
docker ps

# 查看日志
docker logs shimeji-docs -f

# 查看资源使用情况
docker stats shimeji-docs

# 进入容器调试
docker exec -it shimeji-docs /bin/sh
```

#### 备份和恢复

```bash
# 备份配置文件
tar -czf backup-$(date +%Y%m%d).tar.gz docker-compose.prod.yml nginx.conf ssl/

# 如果需要数据备份（本项目是静态站点，通常不需要）
# docker run --rm -v shimeji_data:/data -v $(pwd):/backup alpine tar czf /backup/data-backup.tar.gz /data
```

## 文档编写

- 用户文档位于 `user/` 目录
- 开发文档位于 `development/` 目录
- 配置文件位于 `.vitepress/config.js`

## 项目链接

- 主项目：[https://github.com/DCRepairCenter/ShimejiLive](https://github.com/DCRepairCenter/ShimejiLive)
- 文档站点：部署在 GitHub Pages
