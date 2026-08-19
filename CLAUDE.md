# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

**Compile and package** (closes app first if the JAR is in use):
```bash
bash build.sh
```
If the app is open (JAR locked), compile manually:
```bash
find src -name "*.java" > /tmp/sources.txt
javac -encoding UTF-8 -cp "dist/libs/mysql-connector-j-9.7.0.jar" -d out @/tmp/sources.txt
jar cfm dist/GearGestGarage.jar build/MANIFEST.MF -C out .
```

**Start MySQL** (required before running the app):
```bash
docker compose up -d mysql
```

**Run the app** (from project root, after building):
```bash
java -cp "dist/GearGestGarage.jar;dist/libs/mysql-connector-j-9.7.0.jar" view.Main
```

**Apply a DB migration** (without recreating the database):
```bash
docker exec gear_gest_mysql mysql --default-character-set=utf8mb4 -u root -proot GearGestGarage -e "ALTER TABLE ..."
```

**Recreate the database from scratch** (destroys all data). `db/schema.sql` holds pure structure (DDL only); `db/seed.sql` holds the initial data (oficina, admin user, service catalog) and must run after it:
```bash
cat db/schema.sql | docker exec -i gear_gest_mysql mysql --default-character-set=utf8mb4 -u root -proot
cat db/seed.sql   | docker exec -i gear_gest_mysql mysql --default-character-set=utf8mb4 -u root -proot GearGestGarage
```
> **Always pass `--default-character-set=utf8mb4`** to the `mysql` CLI. Without it the client defaults to `latin1`, and since the schema is `utf8mb4`, MySQL silently double-encodes every accented character on insert (e.g. `óleo` gets stored as `Ã³leo`) — this is independent of which shell you pipe from. Also prefer `cat` over PowerShell's `Get-Content` for piping SQL files; `Get-Content` needs an explicit `-Encoding utf8` or it can mangle non-ASCII bytes on read.

Default credentials: `oficina@geargest.com` / `123456`. DB connection config: `db.properties` (optional — defaults to localhost:3307, root/root, matching `docker-compose.yml`'s exposed port).

There are no automated tests.

## Architecture

**Entry point**: `src/main/java/view/Main.java` — boots Swing on the EDT, creates the single `OficinaController`, opens `V_Login`.

**Navigation**: Every `JPanel` navigates by calling:
```java
SwingUtilities.getWindowAncestor(this) → (V_Main) → atualizarConteudo(newPanel)
```
`V_Main` is the outer window shell; `V_MenuLateral` is the persistent sidebar.

**Facade (`controller/OficinaController.java`)**: The single object passed to every view. All UI calls go through it. It delegates to the backend via the bridge.

**Bridge (`controller/OficinaController_backendBridge.java`)**: Wires all backend controllers/repositories from a single `Conexao`. Package-private; only `OficinaController` holds a reference.

**Backend modules** (`src/main/java/br/com/oficina/`):

| Package | Responsibility |
|---|---|
| `oficina/` | Workshop entity (Oficina) |
| `usuario/` | Users — `ClienteController`, `FuncionarioController`, `UsuarioController` share one `UsuarioRepository` |
| `veiculo/` | Vehicles, models, makes |
| `estoque/` | Parts (`PecaController/Service/Repository`) |
| `atendimento/` | Budgets (`OrcamentoController`), service orders (`ServicoController`), service catalog (`CatalogoServicoController`), part links (`OrcamentoPecaRepository`) |
| `shared/config/` | DB infrastructure (see below) |

Each module follows Controller → Service → Repository, one class per layer.

**Custom JDBC ORM** (`shared/config/`):
- `Conexao` — wraps a single JDBC `Connection`; holds registered `Tabela` instances
- `Tabela` — represents one DB table; provides `inserir`, `buscarPorId`, `filtrar`, `remover`, `atualizar`. **`filtrar()` loads all rows into memory first**, then applies the Java `Predicate` — avoid it on large tables
- `Registro` — `Map<String, String>` (all values are strings); used as both input and output for `Tabela` operations
- `DatabaseConfig` — singleton; reads `db.properties` → env vars → defaults. Calls `registrarMapeamentos()` once at startup. **Every table column must be registered here** or the Tabela won't read/write it

**Seed**: `Seed.java` creates the oficina and the admin user on first run. `SeedLoader.java` reads `src/main/resources/dados_seed.txt` at first launch to populate sample data (montadoras, modelos, clientes, veículos).

**`listarPecasOrcamentoComValor()`** returns `Object[]{PecaEntity, Double valor, String nomeTecnico, String fabricante}` (4 elements). Consumers must guard with `triple.length > N`.

## Visual Style

White background (`Color.WHITE`) + orange accent `#FF9900`. Font: Segoe UI. Border: `RoundedBorder` inner class (radius 6, `#CCCCCC`). Form error state: red border via `marcarErro(field)` / `limparErro(field)`.

All form panels follow the same pattern: `DocumentFilter` for input filtering, `validarFormulario()` returning boolean, `JOptionPane` for error lists.

## Adding a New Column to a Table

1. Add the column to `db/schema.sql`
2. Add the column name to the matching `new Tabela(...)` call in `DatabaseConfig.registrarMapeamentos()`
3. Update the relevant `Repository` methods (`vincular`, `listarXxx`)
4. Propagate through Service → Controller → facade (`OficinaController`) → views
5. Apply migration: `docker exec gear_gest_mysql mysql --default-character-set=utf8mb4 -u root -proot GearGestGarage -e "ALTER TABLE t ADD COLUMN ..."`
