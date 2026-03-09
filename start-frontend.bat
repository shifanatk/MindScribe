@echo off
echo Starting MindScribe Frontend...
echo.

REM Wait for backend to be ready
echo Checking if backend is ready...
timeout /t 5 /nobreak >nul

REM Start frontend
echo Starting JavaFX frontend...
start "MindScribe Frontend" cmd /k "mvn javafx:run -Pfrontend"

echo Frontend starting...
echo.
echo Both backend and frontend should now be running.
echo Backend: http://localhost:8080
echo Frontend: JavaFX Desktop Application
echo.
pause
