# 项目优化说明

## 优化内容

### 1. 端口配置优化
- 后端服务端口从 8080 改为 8081，避免端口冲突
- 前端开发服务器自动选择可用端口（5176）
- 更新了前端API配置以匹配新的后端端口

### 2. 启动脚本优化
- 创建了 `run-app.bat` 脚本，一键启动整个应用
- 创建了 `start-app.bat` 脚本，提供启动说明
- 优化了README.md文档，包含最新的访问地址

### 3. 架构优化
- 基础设施服务（MySQL、Redis、MinIO）通过Docker Compose管理
- 后端Spring Boot应用使用8081端口
- 前端Vue应用使用Vite开发服务器

## 服务地址

- **前端页面**: http://localhost:5176
- **后端API**: http://localhost:8081/api
- **数据库(MySQL)**: localhost:3307
- **缓存(Redis)**: localhost:6379
- **文件服务(MinIO)**: http://localhost:9001

## 启动方式

### 方式一：使用一键启动脚本
双击运行 `run-app.bat` 脚本，自动启动所有服务。

### 方式二：手动启动
1. 启动基础设施：`docker-compose up -d`
2. 启动后端：`cd springboot-demo && mvn spring-boot:run`
3. 启动前端：`cd vue-demo && npm run dev`

## 项目特性

这是一个完整的户外社交平台，包含以下功能模块：
- 用户注册登录
- 发布户外活动和路线
- 装备租赁服务
- 社交互动功能（帖子、评论、点赞）
- 路线规划与分享
- 装备清单管理

## 技术栈

- **后端**: Spring Boot 2.7 + MyBatis + MySQL + Redis + MinIO
- **前端**: Vue 3 + Vite + Element Plus
- **基础设施**: Docker Compose