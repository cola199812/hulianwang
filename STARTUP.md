# 启动步骤与自测说明

## 准备数据库
- 安装并启动 MySQL，本地端口默认 3306
- 执行 `db.sql`：
  - 使用客户端（如 MySQL Workbench 或命令行）连接后执行本仓库根目录的 `db.sql`
  - 创建数据库 `outdoor_demo` 与四张表及索引

## 配置后端
- 路径：`springboot-demo/src/main/resources/application.yml`
- 修改数据源：
  - `spring.datasource.url`：如 `jdbc:mysql://localhost:3306/outdoor_demo?...`
  - `spring.datasource.username` 与 `spring.datasource.password` 改为你的 MySQL 账号
- 端口默认 `8080`

## 启动后端
- 确保已启动 MinIO 服务（见下方“MinIO 配置”章节）
- 进入 `springboot-demo` 目录
- 安装 Maven 依赖并启动：
  - `mvn spring-boot:run`
- 后端启动成功后监听 `http://localhost:8080/`

## MinIO 配置（图片/视频存储）
- 下载 MinIO Server: https://dl.min.io/server/minio/release/windows-amd64/minio.exe
- 启动命令（PowerShell）:
  ```powershell
  # 默认账号密码均为 minioadmin
  .\minio.exe server D:\minio_data --console-address ":9001"
  ```
- 访问控制台: `http://localhost:9001`
- 确保 Bucket `outdoor-media` 存在（代码会自动创建，但需保证服务运行）

## 启动前端
- 进入 `vue-demo` 目录
- 安装依赖：
  - `npm install`
- 启动开发服务器：
  - `npm run dev`
- 前端默认访问 `http://localhost:5173/`

## 自测流程
- 打开前端：`http://localhost:5173/`
- 在登录页：
  - 可先注册一个用户，再登录（登录后会在后端 Session 写入 `userId`）
  - 登录成功后点击进入“路线列表”
- 路线列表页：
  - 点击“创建路线”进入创建页，提交后返回列表可查看
  - 点击“详情”查看路线详情
- 活动列表页：
  - 在表单区填写“活动名称、路线ID、时间、最大人数”，点击“创建活动”
  - 在表格中点击“报名”，当前人数会刷新
  - 若人数达到上限或重复报名，会提示失败

## 注意事项
- 本 Demo 使用 Session 维持登录态，前端已开启 `withCredentials`，后端开启了 CORS（允许凭证）
- 活动的当前人数通过 `activity_user` 表计数得到，并未在 `activity` 表冗余存储
- 注册密码以 BCrypt 哈希存储


## 12.17 
修改mysql user表  添加 了phone emil字段