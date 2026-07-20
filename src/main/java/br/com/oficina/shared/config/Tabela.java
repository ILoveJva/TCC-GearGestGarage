package br.com.oficina.shared.config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Representa uma tabela MySQL. Mantem a mesma API publica do engine anterior
 * (inserir, buscarPorId, filtrar, registros), agora executando SQL real via JDBC.
 */
public class Tabela {
    private final String nome;
    private final String chavePrimaria;
    private final List<String> colunas;
    private Conexao conexao;

    public Tabela(String nome, String chavePrimaria, String... colunas) {
        this.nome = nome;
        this.chavePrimaria = chavePrimaria;
        this.colunas = new ArrayList<>(List.of(colunas));
    }

    void vincular(Conexao c) { this.conexao = c; }

    public String nome() { return nome; }
    public List<String> colunas() { return colunas; }
    public String chavePrimaria() { return chavePrimaria; }

    private Connection conn() { return conexao.jdbc(); }

    /** Colunas exceto a PK (que e AUTO_INCREMENT). */
    private List<String> colunasInsercao() {
        List<String> out = new ArrayList<>();
        for (String c : colunas) if (!c.equals(chavePrimaria)) out.add(c);
        return out;
    }

    /** INSERT; retorna o id gerado e tambem o grava no proprio Registro. */
    public long inserir(Registro r) {
        List<String> cols = new ArrayList<>();
        List<Object> valores = new ArrayList<>();
        for (String c : colunasInsercao()) {
            Object val = valorOuNull(r.get(c));
            if (val != null) {
                cols.add(c);
                valores.add(val);
            }
        }

        if (cols.isEmpty()) return 0L;

        String campos = String.join(", ", cols);
        String marcadores = String.join(", ", cols.stream().map(c -> "?").toArray(String[]::new));
        String sql = "INSERT INTO " + nome + " (" + campos + ") VALUES (" + marcadores + ")";

        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            for (Object val : valores) ps.setObject(i++, val);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    r.set(chavePrimaria, id);
                    return id;
                }
            }
            return 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir em " + nome + ": " + e.getMessage(), e);
        }
    }

    public Registro buscarPorId(long id) {
        String sql = "SELECT * FROM " + nome + " WHERE " + chavePrimaria + " = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar em " + nome + ": " + e.getMessage(), e);
        }
    }

    public List<Registro> registros() {
        List<Registro> out = new ArrayList<>();
        String sql = "SELECT * FROM " + nome;
        try (Statement st = conn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) out.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar " + nome + ": " + e.getMessage(), e);
        }
        return out;
    }

    /** Filtro aplicado em memoria sobre todos os registros (compativel com os lambdas existentes). */
    public List<Registro> filtrar(Predicate<Registro> p) {
        List<Registro> out = new ArrayList<>();
        for (Registro r : registros()) if (p.test(r)) out.add(r);
        return out;
    }

    public boolean remover(long id) {
        String sql = "DELETE FROM " + nome + " WHERE " + chavePrimaria + " = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover de " + nome + ": " + e.getMessage(), e);
        }
    }

    /** UPDATE de uma unica coluna por id. */
    public boolean atualizar(long id, String coluna, Object valor) {
        String sql = "UPDATE " + nome + " SET " + coluna + " = ? WHERE " + chavePrimaria + " = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setObject(1, valor);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar " + nome + ": " + e.getMessage(), e);
        }
    }

    /** Converte uma linha do ResultSet em Registro (todos os valores como String, como no engine antigo). */
    private Registro mapear(ResultSet rs) throws SQLException {
        Registro r = new Registro();
        for (String c : colunas) {
            Object v = rs.getObject(c);
            r.set(c, v == null ? "" : String.valueOf(v));
        }
        return r;
    }

    /** Campos vazios viram NULL no banco (evita "" em colunas numericas/FK). */
    private Object valorOuNull(String valor) {
        return (valor == null || valor.isEmpty()) ? null : valor;
    }
}
