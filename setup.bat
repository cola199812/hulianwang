@echo off
chcp 65001 >nul
echo =======================================================
echo        Outdoor Social Platform - Setup Script
echo =======================================================
echo.
echo [1/3] Checking Docker environment...
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker is NOT running!
    echo Please start Docker Desktop and wait for it to initialize.
    pause
    exit
)

echo [2/3] Starting services (MySQL, Redis, MinIO)...
echo This may take a few minutes to pull images...
docker-compose up -d

if %errorlevel% neq 0 (
    echo [ERROR] Failed to start services. Please check if Docker is running.
    pause
    exit
)

echo.
echo [3/3] Service Status...
echo -------------------------------------------------------
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo -------------------------------------------------------
echo.
echo [SUCCESS] Environment is ready!
echo.
echo Database Port: 3307 (User: root / Pass: qq2027681066)
echo Redis Port:    6379
echo MinIO Console: http://localhost:9001
echo.
echo You can now run the backend and frontend code.
echo Press any key to exit...
pause >nul
