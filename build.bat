@echo off
REM Compila e empacota o Gear Gest Garage em um JAR executavel (Windows).
cd /d "%~dp0"
echo [1/4] Limpando...
if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out
mkdir dist\libs
echo [2/4] Compilando...
if exist sources.txt del sources.txt
powershell -NoProfile -Command "Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { '\"' + ($_.FullName -replace '\\','/') + '\"' } | Set-Content -Encoding ascii sources.txt"
javac -encoding UTF-8 -d out @sources.txt
del sources.txt
if errorlevel 1 (
    echo ERRO na compilacao. Abortando.
    exit /b 1
)
echo [3/4] Copiando recursos...
if exist src\main\resources xcopy /s /e /y src\main\resources\* out\ >nul
echo [4/4] Gerando JAR...
jar cfm dist\GearGestGarage.jar build\MANIFEST.MF -C out .
xcopy /s /e /y db dist\db\ >nul
if not exist libs\mysql-connector-j-9.7.0.jar (
    echo Baixando driver MySQL Connector/J...
    curl -sL -o libs\mysql-connector-j-9.7.0.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.7.0/mysql-connector-j-9.7.0.jar
)
copy libs\mysql-connector-j-9.7.0.jar dist\libs\ >nul
echo.
echo OK -^> dist\GearGestGarage.jar
echo Rode:  java -jar dist\GearGestGarage.jar
echo (usa MySQL em localhost:3307 por padrao - suba com: docker compose up -d mysql)
