# 青年户外社交平台 (Outdoor Social Platform)

这是一个基于 Spring Boot 和 Vue 3 的全栈户外社交平台项目。

## 🚀 快速开始

### 1. 环境准备
- **JDK 17+**
- **Node.js 16+**
- **Maven 3.6+**
- **Docker Desktop** (必须安装及配置)

#### 🐳 Docker 配置指南 (Windows)
1. **下载安装**: 访问 [Docker 官网](https://www.docker.com/products/docker-desktop/) 下载并安装 Docker Desktop for Windows。
2. **启动**: 安装完成后启动 Docker Desktop，等待左下角状态变为绿色 (Engine running)。
3. **配置镜像源** (推荐):
   为了加快镜像下载速度，建议配置国内镜像源。
   - 打开设置 (齿轮图标) -> **Docker Engine**
   - 修改 JSON 配置如下：
     ```json
     {
       "builder": {
         "gc": {
           "defaultKeepStorage": "20GB",
           "enabled": true
         }
       },
       "experimental": false,
       "registry-mirrors": [
         "https://docker.m.daocloud.io",
         "https://docker.1panel.live"
       ]
     }
     ```
   - 点击 **Apply & restart** 保存并重启。
4. **验证**: 打开终端 (PowerShell) 输入 `docker -v`，显示版本号即为成功。

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

### 3. 给别人使用（一键配置）

如果您想把这个项目分享给同学或老师，且不希望他们手动配置复杂的数据库账号密码，请按照以下步骤：

1. **打包项目**: 将整个项目文件夹（包含 `setup.bat` 和 `docker-compose.yml`）压缩发送给对方。
2. **对方操作**:
   - 对方只需安装好 Docker Desktop。
   - 双击运行项目根目录下的 **`setup.bat`**。
   
   脚本会自动：
   - 检查 Docker 是否安装。
   - 自动拉取并启动 MySQL, Redis, MinIO。
   - 自动配置好所有账号密码（默认配置在 `docker-compose.yml` 中，对方无需修改）。

3. **运行代码**:
   环境启动后，对方只需像往常一样运行后端 (Idea/Maven) 和前端 (VSCode/Npm) 即可，无需关心数据库配置。

### 4. 运行后端 (Spring Boot)
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

### 5. 运行前端 (Vue 3)
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
