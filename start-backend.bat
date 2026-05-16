@echo off
setlocal

set "PROJECT_DIR=C:\Users\17890\code\WcInv"
set "BACKEND_DIR=%PROJECT_DIR%\backend\wcinv-api"
set "PORT=8080"

echo [WcInv] Restarting backend on port %PORT%...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%PORT%" ^| findstr "LISTENING"') do (
    echo [WcInv] Stopping old backend process PID %%a...
    taskkill /PID %%a /F >nul 2>nul
)

cd /d "%BACKEND_DIR%"
if errorlevel 1 (
    echo [WcInv] Backend directory not found: %BACKEND_DIR%
    pause
    exit /b 1
)

echo [WcInv] Starting backend...
echo [WcInv] API: http://localhost:8080
echo [WcInv] Press Ctrl+C to stop.
mvn spring-boot:run

echo.
echo [WcInv] Backend stopped.
pause
