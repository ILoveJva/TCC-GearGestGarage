# Rodando com Docker Compose (banco + app juntos)

Sobe o MySQL (ja com o banco GearGestGarage e as tabelas criadas
automaticamente) e a aplicacao Java, com um unico comando.

## Pre-requisito
- Docker Desktop instalado e aberto.

## A parte que exige atencao: a interface grafica (Swing)
A aplicacao abre uma JANELA (Swing). Um container nao tem tela propria, entao
ele precisa "emprestar" o display do seu computador. O passo muda por sistema:

### Linux (mais simples)
```bash
xhost +local:docker          # libera o container a usar seu display
docker compose --profile full up --build
```

### Windows
1. Instale um servidor X, ex.: VcXsrv (https://sourceforge.net/projects/vcxsrv/).
2. Abra o XLaunch e marque "Disable access control".
3. Rode:
```powershell
docker compose --profile full up --build
```
(O compose ja usa DISPLAY=host.docker.internal:0, que e o esperado no Windows.)

### macOS
1. Instale o XQuartz (https://www.xquartz.org/) e abra-o.
2. Em XQuartz > Preferencias > Seguranca, marque "Allow connections from network clients".
3. No terminal:
```bash
xhost + 127.0.0.1
docker compose --profile full up --build
```

## Comandos uteis
```bash
docker compose --profile full up --build   # sobe banco + app (primeira vez compila o jar)
docker compose up -d mysql                 # sobe SO o banco, em segundo plano
docker compose logs -f app                 # ve os logs da aplicacao
docker compose down                        # para tudo
docker compose down -v                     # para tudo E APAGA os dados do banco
```

## Como funciona (resumo)
- O servico "mysql" cria o banco GearGestGarage (variavel MYSQL_DATABASE) e roda
  db/schema.sql + db/seed.sql na primeira subida (pasta
  /docker-entrypoint-initdb.d, nessa ordem). Nas proximas vezes, pula (dados ja existem).
- O servico "app" so sobe com `--profile full` (ver README) - por padrao, `docker
  compose up`/`up -d mysql` nao tenta iniciar a app.
- O servico "app" so inicia quando o banco esta saudavel (depends_on healthcheck).
- Dentro do Docker, a app acessa o banco pelo nome do servico ("mysql"), nao por
  localhost. Isso vem pronto na variavel DB_URL do compose.
- O jar e o driver JDBC sao construidos dentro da imagem (Dockerfile), entao voce
  NAO precisa baixar o connector manualmente neste modo.

## Se preferir SO o banco no Docker e a app na sua maquina
E mais simples (sem X11):
```bash
docker compose up -d mysql
# depois, na sua maquina:
java -jar dist/GearGestGarage.jar
```
Nesse caso a app usa os valores padrao (localhost:3307, root/root), que ja
batem com a porta exposta pelo compose - nenhuma configuracao extra e necessaria.
