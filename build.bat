@echo off
REM Compila e empacota o Gear Gest Garage em um JAR executavel (Windows).
cd /d "%~dp0"
echo [1/5] Limpando...
if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out
mkdir dist\libs
echo [2/5] Compilando...
if exist sources.txt del sources.txt
powershell -NoProfile -Command "Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { '\"' + ($_.FullName -replace '\\','/') + '\"' } | Set-Content -Encoding ascii sources.txt"
javac -encoding UTF-8 -d out @sources.txt
del sources.txt
if errorlevel 1 (
    echo ERRO na compilacao. Abortando.
    exit /b 1
)
echo [3/5] Copiando recursos...
if exist src\main\resources xcopy /s /e /y src\main\resources\* out\ >nul
echo [4/5] Gerando JAR...
jar cfm dist\GearGestGarage.jar build\MANIFEST.MF -C out .
xcopy /s /e /y db dist\db\ >nul
if not exist libs\mysql-connector-j-9.7.0.jar (
    echo Baixando driver MySQL Connector/J...
    curl -sL -o libs\mysql-connector-j-9.7.0.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.7.0/mysql-connector-j-9.7.0.jar
)
copy libs\mysql-connector-j-9.7.0.jar dist\libs\ >nul

echo [5/5] Criando atalho na area de trabalho...
set "SHORTCUT_PS1=%TEMP%\gear_gest_shortcut.ps1"
echo $javaw = (Get-Command javaw.exe -ErrorAction SilentlyContinue).Source > "%SHORTCUT_PS1%"
echo if (-not $javaw) { $javaw = 'javaw.exe' } >> "%SHORTCUT_PS1%"
echo $desktop = [Environment]::GetFolderPath('Desktop') >> "%SHORTCUT_PS1%"
echo $ws = New-Object -ComObject WScript.Shell >> "%SHORTCUT_PS1%"
echo $lnk = $ws.CreateShortcut((Join-Path $desktop 'GearGestGarage.lnk')) >> "%SHORTCUT_PS1%"
echo $lnk.TargetPath = $javaw >> "%SHORTCUT_PS1%"
echo $lnk.Arguments = '-jar "%~dp0dist\GearGestGarage.jar"' >> "%SHORTCUT_PS1%"
echo $lnk.WorkingDirectory = '%~dp0' >> "%SHORTCUT_PS1%"
echo $lnk.IconLocation = $javaw >> "%SHORTCUT_PS1%"
echo $lnk.Description = 'Gear Gest Garage' >> "%SHORTCUT_PS1%"
echo $lnk.Save() >> "%SHORTCUT_PS1%"
powershell -NoProfile -ExecutionPolicy Bypass -File "%SHORTCUT_PS1%"
del "%SHORTCUT_PS1%"

echo.
echo OK -^> dist\GearGestGarage.jar
echo Rode:  java -jar dist\GearGestGarage.jar
echo (usa MySQL em localhost:3307 por padrao - suba com: docker compose up -d mysql)
echo Atalho criado na area de trabalho: GearGestGarage.lnk
