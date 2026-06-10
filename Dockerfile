# ---------- Estagio 1: build (compila o JAR e baixa o driver) ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Baixa o driver JDBC do MySQL (Connector/J)
ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.7.0/mysql-connector-j-9.7.0.jar /app/libs/mysql-connector-j-9.7.0.jar

# Copia o codigo e o manifest
COPY src ./src
COPY build/MANIFEST.MF ./build/MANIFEST.MF

# Compila e empacota
RUN mkdir -p out && \
    find src -name "*.java" > sources.txt && \
    javac -d out @sources.txt && \
    jar cfm GearGestGarage.jar build/MANIFEST.MF -C out .

# ---------- Estagio 2: runtime (so o necessario para rodar) ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/GearGestGarage.jar ./GearGestGarage.jar
COPY --from=build /app/libs ./libs
# A interface Swing precisa de um display X11 (fornecido via DISPLAY no compose)
ENTRYPOINT ["java", "-jar", "GearGestGarage.jar"]
