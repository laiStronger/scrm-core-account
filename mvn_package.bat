cd /d %~dp0
call mvn clean package assembly:assembly -P alpha
pause