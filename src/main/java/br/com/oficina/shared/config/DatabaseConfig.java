package br.com.oficina.shared.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuracao central do banco de dados (MySQL).
 * Abre a conexao JDBC unica e registra os mapeamentos de tabela usados pelos
 * repositories. A criacao das tabelas em si fica no script db/schema.sql.
 *
 * Configuracao (em ordem de prioridade):
 *   1) arquivo db.properties (db.url, db.user, db.password)
 *   2) variaveis de ambiente DB_URL, DB_USER, DB_PASSWORD
 *   3) valores padrao (localhost:3306/GearGestGarage, root, root)
 */
public final class DatabaseConfig {

    private static Conexao conexao;
    /** Mantido por compatibilidade com chamadas existentes (valor ignorado no MySQL). */
    public static final String DIRETORIO_PADRAO = "mysql";

    private static final String URL_PADRAO =
        "jdbc:mysql://localhost:3306/GearGestGarage?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER_PADRAO = "root";
    private static final String PASS_PADRAO = "";   // XAMPP: root sem senha por padrao

    private DatabaseConfig() {}

    public static synchronized Conexao inicializar(String ignorado) {
        if (conexao != null) return conexao;

        Properties cfg = carregarConfig();
        String url  = cfg.getProperty("db.url",      System.getenv().getOrDefault("DB_URL", URL_PADRAO));
        String user = cfg.getProperty("db.user",     System.getenv().getOrDefault("DB_USER", USER_PADRAO));
        String pass = cfg.getProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", PASS_PADRAO));

        conexao = new Conexao(url, user, pass);
        conexao.abrir();
        registrarMapeamentos(conexao);
        return conexao;
    }

    private static Properties carregarConfig() {
        Properties p = new Properties();
        try (InputStream in = new FileInputStream("db.properties")) {
            p.load(in);
        } catch (Exception ignored) {
            // sem arquivo: usa env/defaults
        }
        return p;
    }

    public static Conexao conexao() {
        if (conexao == null) inicializar(DIRETORIO_PADRAO);
        return conexao;
    }

    public static void fechar() {
        if (conexao != null) { conexao.fechar(); conexao = null; }
    }

    /** Registra o mapeamento (nome, PK e colunas) de cada tabela. As tabelas sao criadas pelo db/schema.sql. */
    private static void registrarMapeamentos(Conexao con) {
        con.registrarTabela(new Tabela("oficina", "id_oficina",
            "id_oficina", "nome", "endereco", "telefone", "cnpj"));

        con.registrarTabela(new Tabela("usuario", "id_usuario",
            "id_usuario", "nome", "email", "senha", "telefone", "id_oficina"));

        con.registrarTabela(new Tabela("cliente", "id_cliente",
            "id_cliente", "id_usuario"));

        con.registrarTabela(new Tabela("funcionario", "id_funcionario",
            "id_funcionario", "nome", "cargo", "id_usuario"));

        con.registrarTabela(new Tabela("montadora", "id_montadora",
            "id_montadora", "nome", "pais_origem"));

        con.registrarTabela(new Tabela("modelo", "id_modelo",
            "id_modelo", "nome", "ano", "id_montadora"));

        con.registrarTabela(new Tabela("fabricante_peca", "id_fabricante_peca",
            "id_fabricante_peca", "nome", "pais"));

        con.registrarTabela(new Tabela("veiculo", "id_veiculo",
            "id_veiculo", "placa", "tipo_veiculo", "id_cliente", "id_modelo"));

        con.registrarTabela(new Tabela("detalhes_veiculo", "id_detalhes",
            "id_detalhes", "id_veiculo", "motor", "cambio", "direcao",
            "sistema_freios", "cor", "vin"));

        con.registrarTabela(new Tabela("peca", "id_peca",
            "id_peca", "nome_peca", "vida_util_tempo", "vida_util_km",
            "id_fabricante_peca", "id_veiculo"));

        con.registrarTabela(new Tabela("orcamento", "id_orcamento",
            "id_orcamento", "valor", "id_peca", "id_veiculo", "id_cliente", "id_funcionario"));

        con.registrarTabela(new Tabela("servico", "id_servico",
            "id_servico", "data_servico", "km_servico", "titulo", "tipo_servico",
            "status", "id_veiculo", "id_oficina", "id_orcamento"));

        con.registrarTabela(new Tabela("item_servico", "id_item_servico",
            "id_item_servico", "descricao", "status", "data_realizacao",
            "id_peca", "id_servico", "id_funcionario"));
    }
}
