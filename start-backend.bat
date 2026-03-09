@echo off
echo Starting MindScribe Backend...
echo.

REM Kill any existing Java processes
echo Stopping any existing Java processes...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 /nobreak >nul

REM Start backend
echo Starting backend on port 8080...
start "MindScribe Backend" cmd /k "mvn -DskipTests spring-boot:run"

echo Backend starting...
echo Wait 30 seconds for backend to fully start before launching frontend.
echo.
pause
