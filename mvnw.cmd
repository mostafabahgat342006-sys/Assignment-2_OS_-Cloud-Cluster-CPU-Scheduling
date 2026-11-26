@echo off
setlocal
set "MAVEN_CMD=mvn"
where mvn >nul 2>&1
if %ERRORLEVEL% equ 0 (
    mvn %*
    exit /b %ERRORLEVEL%
)
if exist "C:\Users\taree\AppData\Local\Programs\apache-maven-3.9.6\bin\mvn.cmd" (
    "C:\Users\taree\AppData\Local\Programs\apache-maven-3.9.6\bin\mvn.cmd" %*
    exit /b %ERRORLEVEL%
)
echo Error: Maven could not be found. Please ensure Apache Maven is installed and on your PATH.
exit /b 1
