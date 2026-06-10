@echo off
REM Compila e empacota o Gear Gest Garage em um JAR executavel (Windows).
cd /d "%~dp0"

echo [1/4] Limpando...
if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out
mkdir dist\libs

echo [2/4] Compilando...
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
del sources.txt

echo [3/4] Copiando recursos...
if exist src\main\resources xcopy /s /e /y src\main\resources\* out\ >nul

echo [4/4] Gerando JAR...
jar cfm dist\GearGestGarage.jar build\MANIFEST.MF -C out .

copy db.properties.exemplo dist\db.properties >nul
xcopy /s /e /y db dist\db\ >nul
if exist libs\mysql-connector-j-9.7.0.jar (copy libs\mysql-connector-j-9.7.0.jar dist\libs\ >nul) else (copy libs\LEIA-ME.txt dist\libs\ >nul)

echo.
echo OK -^> dist\GearGestGarage.jar
echo Coloque o driver em dist\libs\ e rode:  java -jar dist\GearGestGarage.jar
