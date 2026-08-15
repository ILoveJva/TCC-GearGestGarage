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
db/                schema.sql (estrutura) + seed.sql (dados iniciais)
Dockerfile         Imagem da aplicacao (compila o jar e baixa o driver)
docker-compose.yml Sobe MySQL (e, opcionalmente, a app)
build.bat / build.sh  Compila e gera dist/GearGestGarage.jar
```

## Como executar

**Pré-requisitos:** Java 21+ e Docker Desktop instalados.

O banco roda em Docker; a aplicação (Swing) roda direto na sua máquina — assim
você não precisa configurar servidor X11 nem lidar com interface gráfica dentro
de container.

### Passo 1 - Subir o banco
```bash
docker compose up -d mysql
```
Cria o banco `GearGestGarage` já com as tabelas (schema) e dados de exemplo (seed),
exposto em `localhost:3307`.

### Passo 2 - Compilar
```bash
build.bat        # Windows
./build.sh       # Linux/Mac
```
Compila o código e baixa o driver MySQL Connector/J automaticamente (uma vez;
fica em `libs/` para os próximos builds).

### Passo 3 - Rodar
```bash
java -jar dist/GearGestGarage.jar
```
A app já vem configurada por padrão para `localhost:3307` (o mesmo que o
Docker expõe), então nenhum arquivo de configuração é necessário. Se seu MySQL
usa outra porta/credenciais, crie um `db.properties` na raiz do projeto com
`db.url`, `db.user`, `db.password` (veja `DatabaseConfig.java` para o formato).

**Login de teste:** oficina@geargest.com / 123456

## Documentacao adicional
- `DOCKER.md` - modo avançado: rodar a app *dentro* do Docker (exige X11 no host).
- `db/` - scripts SQL (estrutura e dados).

## Observacoes
- `db.properties` (se você criar um, com senha) NAO vai para o repositório por
  segurança - já está no `.gitignore`.
- Os dados ficam num volume do Docker (`gear_gest_data`) e persistem entre
  execuções. `docker compose down -v` apaga esse volume.
