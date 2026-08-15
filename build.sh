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
cp -r db dist/db
if [ ! -f libs/mysql-connector-j-9.7.0.jar ]; then
  echo "Baixando driver MySQL Connector/J..."
  curl -sL -o libs/mysql-connector-j-9.7.0.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.7.0/mysql-connector-j-9.7.0.jar
fi
cp libs/mysql-connector-j-9.7.0.jar dist/libs/

echo ""
echo "OK -> dist/GearGestGarage.jar"
echo "Rode:  java -jar dist/GearGestGarage.jar"
echo "(usa MySQL em localhost:3307 por padrao - suba com: docker compose up -d mysql)"
