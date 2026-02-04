@echo off
setlocal
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "%~dp0"
call mvnw.cmd spring-boot:run -DskipTests
endlocal
