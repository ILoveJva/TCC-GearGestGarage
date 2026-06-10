#!/usr/bin/env bash
# Compila e empacota o Gear Gest Garage em um JAR executavel.
set -e
cd "$(dirname "$0")"

echo "[1/4] Limpando..."
rm -rf out dist
mkdir -p out dist/libs

echo "[2/4] Compilando..."
javac -d out $(find src -name "*.java")

echo "[3/4] Copiando recursos (assets, se houver)..."
if [ -d src/main/resources ]; then
  cp -r src/main/resources/* out/ 2>/dev/null || true
fi

echo "[4/4] Gerando JAR..."
jar cfm dist/GearGestGarage.jar build/MANIFEST.MF -C out .

# Monta a pasta dist/ pronta para uso
cp db.properties.exemplo dist/db.properties
cp -r db dist/db
[ -f libs/mysql-connector-j-9.7.0.jar ] && cp libs/mysql-connector-j-9.7.0.jar dist/libs/ || cp libs/LEIA-ME.txt dist/libs/

echo ""
echo "OK -> dist/GearGestGarage.jar"
echo "Coloque o driver em dist/libs/ e rode:  java -jar dist/GearGestGarage.jar"
