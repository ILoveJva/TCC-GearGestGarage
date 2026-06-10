package br.com.oficina.shared.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Conexao JDBC com MySQL. Mantem a mesma API publica usada pelos repositories
 * (tabela, registrarTabela, abrir, fechar, salvar), agora apoiada em um banco real.
 */
public class Conexao {

    private final String url;
    private final String usuario;
    private final String senha;

    private Connection conexao;
    private final Map<String, Tabela> tabelas = new HashMap<>();
    private boolean aberta = false;

    public Conexao(String url, String usuario, String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    public void abrir() {
        try {
            // Driver moderno do MySQL (Connector/J 8+) registra-se sozinho,
            // mas chamamos explicitamente para mensagem de erro clara se faltar.
            try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException ignored) {}
            this.conexao = DriverManager.getConnection(url, usuario, senha);
            this.aberta = true;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao conectar no MySQL (" + url + "): " + e.getMessage(), e);
        }
    }

    public Connection jdbc() {
        if (!aberta) throw new IllegalStateException("Conexao nao esta aberta.");
        return conexao;
    }

    public void registrarTabela(Tabela t) {
        if (!aberta) throw new IllegalStateException("Conexao nao esta aberta.");
        t.vincular(this);
        tabelas.put(t.nome(), t);
    }

    public Tabela tabela(String nome) {
        Tabela t = tabelas.get(nome);
        if (t == null) throw new IllegalArgumentException("Tabela nao registrada: " + nome);
        return t;
    }

    /** No JDBC cada operacao ja persiste; mantido por compatibilidade de API. */
    public void salvar() { /* no-op: persistencia imediata via SQL */ }

    public void fechar() {
        if (aberta) {
            try { if (conexao != null) conexao.close(); } catch (SQLException ignored) {}
            aberta = false;
        }
    }
}
