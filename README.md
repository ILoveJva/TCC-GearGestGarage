# TCC -- Gear Gest Garage

Sistema de gestao para oficina mecanica, desenvolvido como Trabalho de Conclusao
de Curso. Aplicacao desktop em **Java (Swing)** com persistencia em **MySQL**,
estruturada em camadas (View -> Controller/Facade -> Backend modular -> JDBC).

## Tecnologias
- Java 21 (Swing para a interface)
- MySQL 8.4
- JDBC (driver MySQL Connector/J)
- Docker / Docker Compose (para subir o banco)

## Estrutura do projeto
```
src/main/java/
  view/            Telas Swing (login, cadastros, listagens, ordens de servico)
  controller/      Facade que conecta as telas ao backend
  model/           Modelos de apresentacao
  br/com/oficina/  Backend modular (oficina, usuario, veiculo, estoque, atendimento)
                   shared/config -> conexao JDBC, mapeamento de tabelas
db/                schema.sql, schema_phpmyadmin.sql, seed.sql
Dockerfile         Imagem da aplicacao (compila o jar e baixa o driver)
docker-compose.yml Sobe MySQL (e, opcionalmente, a app)
build.bat / build.sh  Compila e gera dist/GearGestGarage.jar
```

## Como executar

Pre-requisitos: Java 21+ e Docker Desktop instalados.

### Passo 1 - Subir o banco (Docker)
```bash
docker compose up -d mysql
```
Isso cria o banco GearGestGarage ja com as tabelas (schema) e dados de exemplo (seed).

### Passo 2 - Configurar a conexao
Copie `db.properties.exemplo` para `db.properties`. Para o banco do Docker, use:
```
db.url=jdbc:mysql://localhost:3306/GearGestGarage?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=root
```

### Passo 3 - Compilar e rodar a aplicacao
Gere o jar (uma vez):
```bash
build.bat        # Windows
./build.sh       # Linux/Mac
```
Baixe o driver MySQL Connector/J (https://dev.mysql.com/downloads/connector/j/)
e coloque o .jar em `dist/libs/`. Depois, de dentro de `dist/`:
```bash
# Windows (separador ';')
java -cp "GearGestGarage.jar;libs\mysql-connector-j-9.7.0.jar" view.Main
# Linux/Mac (separador ':')
java -cp "GearGestGarage.jar:libs/mysql-connector-j-9.7.0.jar" view.Main
```
> Ajuste o nome do .jar para a versao que voce baixou.

**Login de teste:** oficina@geargest.com / 123456

## Documentacao adicional
- `DOCKER.md` - detalhes do Docker, modo "tudo no container" e dicas de X11.
- `db/` - scripts SQL (estrutura e dados).

## Observacoes
- O arquivo `db.properties` (com a senha) NAO vai para o repositorio por seguranca;
  use o `db.properties.exemplo` como base.
- Os dados ficam num volume do Docker (gear_gest_data) e persistem entre execucoes.
  `docker compose down -v` apaga esse volume.
