package br.com.oficina.usuario;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

/** Acesso as tabelas usuario, cliente e funcionario (relacionadas por id_usuario). */
public class UsuarioRepository {
    private final Tabela tUsuario;
    private final Tabela tCliente;
    private final Tabela tFuncionario;

    public UsuarioRepository(Conexao con) {
        this.tUsuario = con.tabela("usuario");
        this.tCliente = con.tabela("cliente");
        this.tFuncionario = con.tabela("funcionario");
    }

    // ---------- usuario ----------
    private UsuarioEntity mapUsuario(Registro r) {
        UsuarioEntity u = new UsuarioEntity(r.getLong("id_usuario"), r.get("nome"), r.get("email"),
            r.get("senha"), r.get("telefone"), r.getLong("id_oficina"));
        u.setCpf(r.get("cpf"));
        return u;
    }

    public UsuarioEntity salvarUsuario(UsuarioEntity u) {
        long id = tUsuario.inserir(new Registro()
            .set("nome", u.getNome()).set("cpf", u.getCpf()).set("email", u.getEmail())
            .set("senha", u.getSenha()).set("telefone", u.getTelefone())
            .set("id_oficina", u.getIdOficina()));
        u.setIdUsuario(id);
        return u;
    }

    public UsuarioEntity buscarUsuario(long id) {
        Registro r = tUsuario.buscarPorId(id);
        return r == null ? null : mapUsuario(r);
    }

    public UsuarioEntity buscarPorEmail(String email) {
        List<Registro> r = tUsuario.filtrar(u -> u.get("email").equalsIgnoreCase(email));
        return r.isEmpty() ? null : mapUsuario(r.get(0));
    }

    public List<UsuarioEntity> listarUsuarios() {
        List<UsuarioEntity> out = new ArrayList<>();
        for (Registro r : tUsuario.registros()) out.add(mapUsuario(r));
        return out;
    }

    // ---------- cliente ----------
    /** Cria usuario + registro de cliente. Retorna o ClienteEntity preenchido. */
    public ClienteEntity salvarCliente(String nome, String cpf, String email, String senha,
                                       String telefone, long idOficina) {
        UsuarioEntity novo = new UsuarioEntity(null, nome, email, senha, telefone, idOficina);
        novo.setCpf(cpf);
        UsuarioEntity u = salvarUsuario(novo);
        long idCliente = tCliente.inserir(new Registro().set("id_usuario", u.getIdUsuario()));
        ClienteEntity c = new ClienteEntity();
        c.setIdCliente(idCliente); c.setIdUsuario(u.getIdUsuario());
        c.setNome(nome); c.setEmail(email); c.setTelefone(telefone); c.setCpf(cpf);
        return c;
    }

    private ClienteEntity mapCliente(Registro r) {
        ClienteEntity c = new ClienteEntity();
        c.setIdCliente(r.getLong("id_cliente"));
        c.setIdUsuario(r.getLong("id_usuario"));
        UsuarioEntity u = buscarUsuario(r.getLong("id_usuario"));
        if (u != null) { c.setNome(u.getNome()); c.setEmail(u.getEmail()); c.setTelefone(u.getTelefone()); c.setCpf(u.getCpf()); }
        return c;
    }

    public ClienteEntity buscarCliente(long idCliente) {
        Registro r = tCliente.buscarPorId(idCliente);
        return r == null ? null : mapCliente(r);
    }

    public List<ClienteEntity> listarClientes() {
        List<ClienteEntity> out = new ArrayList<>();
        for (Registro r : tCliente.registros()) out.add(mapCliente(r));
        return out;
    }

    public boolean atualizarCliente(long idCliente, String nome, String cpf, String email, String telefone) {
        ClienteEntity c = buscarCliente(idCliente);
        if (c == null) return false;
        tUsuario.atualizar(c.getIdUsuario(), "nome", nome);
        tUsuario.atualizar(c.getIdUsuario(), "cpf", cpf);
        tUsuario.atualizar(c.getIdUsuario(), "email", email);
        tUsuario.atualizar(c.getIdUsuario(), "telefone", telefone);
        return true;
    }

    public void removerCliente(long idCliente) {
        ClienteEntity c = buscarCliente(idCliente);
        if (c == null) return;
        try {
            tCliente.remover(idCliente);
            tUsuario.remover(c.getIdUsuario());
        } catch (RuntimeException e) {
            throw new RuntimeException("Não é possível excluir: cliente possui veículos ou orçamentos vinculados.", e);
        }
    }

    // ---------- funcionario ----------
    public FuncionarioEntity salvarFuncionario(String nome, String cargo, String endereco, String cpf,
                                               String email, String senha, String telefone, long idOficina) {
        UsuarioEntity novo = new UsuarioEntity(null, nome, email, senha, telefone, idOficina);
        novo.setCpf(cpf);
        UsuarioEntity u = salvarUsuario(novo);
        long idFunc = tFuncionario.inserir(new Registro()
            .set("nome", nome).set("cargo", cargo).set("endereco", endereco)
            .set("id_usuario", u.getIdUsuario()));
        return new FuncionarioEntity(idFunc, nome, cargo, endereco, u.getIdUsuario());
    }

    private FuncionarioEntity mapFuncionario(Registro r) {
        FuncionarioEntity f = new FuncionarioEntity(r.getLong("id_funcionario"), r.get("nome"),
            r.get("cargo"), r.get("endereco"), r.getLong("id_usuario"));
        UsuarioEntity u = buscarUsuario(r.getLong("id_usuario"));
        if (u != null) { f.setEmail(u.getEmail()); f.setTelefone(u.getTelefone()); f.setCpf(u.getCpf()); }
        return f;
    }

    public FuncionarioEntity buscarFuncionario(long idFuncionario) {
        Registro r = tFuncionario.buscarPorId(idFuncionario);
        return r == null ? null : mapFuncionario(r);
    }

    public List<FuncionarioEntity> listarFuncionarios() {
        List<FuncionarioEntity> out = new ArrayList<>();
        for (Registro r : tFuncionario.registros()) out.add(mapFuncionario(r));
        return out;
    }

    public boolean atualizarFuncionario(long idFuncionario, String nome, String cargo, String endereco,
                                        String cpf, String email, String telefone) {
        FuncionarioEntity f = buscarFuncionario(idFuncionario);
        if (f == null) return false;
        tFuncionario.atualizar(idFuncionario, "nome", nome);
        tFuncionario.atualizar(idFuncionario, "cargo", cargo);
        tFuncionario.atualizar(idFuncionario, "endereco", endereco);
        tUsuario.atualizar(f.getIdUsuario(), "nome", nome);
        tUsuario.atualizar(f.getIdUsuario(), "cpf", cpf);
        tUsuario.atualizar(f.getIdUsuario(), "email", email);
        tUsuario.atualizar(f.getIdUsuario(), "telefone", telefone);
        return true;
    }

    public void removerFuncionario(long idFuncionario) {
        FuncionarioEntity f = buscarFuncionario(idFuncionario);
        if (f == null) return;
        try {
            tFuncionario.remover(idFuncionario);
            tUsuario.remover(f.getIdUsuario());
        } catch (RuntimeException e) {
            throw new RuntimeException("Não é possível excluir: funcionário possui orçamentos ou serviços vinculados.", e);
        }
    }
}
