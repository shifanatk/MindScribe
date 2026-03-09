@echo off
echo Stopping all MindScribe processes...
echo.

REM Kill all Java processes
taskkill /F /IM java.exe

echo.
echo All MindScribe processes stopped.
pause
