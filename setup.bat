@echo off
chcp 65001 >nul
echo =======================================================
echo        青年户外社交平台 - 环境一键配置脚本
echo =======================================================
echo.
echo [1/3] 正在检查 Docker 环境...
docker -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Docker，请先安装 Docker Desktop！
    echo 下载地址: https://www.docker.com/products/docker-desktop/
    pause
    exit
)

echo [2/3] 正在启动基础服务 (MySQL, Redis, MinIO)...
echo 这可能需要几分钟下载镜像，请耐心等待...
docker-compose up -d

if %errorlevel% neq 0 (
    echo [错误] 服务启动失败，请检查 Docker 是否正在运行。
    pause
    exit
)

echo.
echo [3/3] 服务检查...
echo -------------------------------------------------------
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo -------------------------------------------------------
echo.
echo ✅ 环境配置成功！
echo.
echo 数据库端口: 3307 (账号: root / 密码: qq2027681066)
echo Redis端口: 6379
echo MinIO控制台: http://localhost:9001
echo.
echo 现在您可以运行后端和前端代码了。
echo 按任意键退出...
pause >nul
