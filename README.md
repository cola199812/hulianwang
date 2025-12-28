# 青年户外社交平台 (Outdoor Social Platform)

这是一个基于 Spring Boot 和 Vue 3 的全栈户外社交平台项目。

## 🚀 快速开始

### 1. 环境准备
- **JDK 17+**
- **Node.js 16+**
- **Docker Desktop** (用于运行数据库和中间件)
- **Maven 3.6+**

### 2. 启动基础设施
项目依赖 MySQL, Redis 和 MinIO。我们使用 Docker Compose 快速启动这些服务。

在项目根目录下运行：
```bash
docker-compose up -d
```
这将启动：
- **MySQL**: 端口 3307 (账号 root / 密码 qq2027681066)
- **Redis**: 端口 6379
- **MinIO**: 控制台端口 9001 (账号 root / 密码 qq2027681066)

### 3. 运行后端 (Spring Boot)
1. 进入后端目录：
   ```bash
   cd springboot-demo
   ```
2. 运行项目：
   ```bash
   mvn spring-boot:run
   ```
   或者使用 IDEA 打开 `springboot-demo` 目录，运行 `DemoApplication` 类。
   
   *后端服务将在 http://localhost:8080 启动*

### 4. 运行前端 (Vue 3)
1. 进入前端目录：
   ```bash
   cd vue-demo
   ```
2. 安装依赖：
   ```bash
   npm install
   ```
3. 启动开发服务器：
   ```bash
   npm run dev
   ```
   
   *前端页面通常在 http://localhost:5173 访问*

## 📝 访问地址
- **前端页面**: [http://localhost:5173](http://localhost:5173)
- **后端 API 文档**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) (如果有集成 Swagger)
- **MinIO 控制台**: [http://localhost:9001](http://localhost:9001)

## 📁 目录结构
- `springboot-demo/`: 后端源码 (Spring Boot)
- `vue-demo/`: 前端源码 (Vue 3 + Vite)
- `docker-compose.yml`: 基础设施容器配置
