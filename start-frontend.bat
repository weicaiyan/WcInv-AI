@echo off
setlocal

set "PROJECT_DIR=C:\Users\17890\code\WcInv"
set "FRONTEND_DIR=%PROJECT_DIR%\frontend"
set "PORT=5173"

echo [WcInv] Restarting frontend on port %PORT%...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%PORT%" ^| findstr "LISTENING"') do (
    echo [WcInv] Stopping old frontend process PID %%a...
    taskkill /PID %%a /F >nul 2>nul
)

cd /d "%FRONTEND_DIR%"
if errorlevel 1 (
    echo [WcInv] Frontend directory not found: %FRONTEND_DIR%
    pause
    exit /b 1
)

echo [WcInv] Starting frontend...
echo [WcInv] Open: http://localhost:5173
echo [WcInv] Press Ctrl+C to stop.
npm run dev -- --host 0.0.0.0

echo.
echo [WcInv] Frontend stopped.
pause
