package br.com.oficina.usuario;

import java.util.List;

public class FuncionarioService {
    private final UsuarioRepository repository;
    public FuncionarioService(UsuarioRepository repository) { this.repository = repository; }

    public FuncionarioEntity cadastrar(String nome, String cargo, String email,
                                       String senha, String telefone, long idOficina) {
        return repository.salvarFuncionario(nome, cargo, email, senha, telefone, idOficina);
    }

    public List<FuncionarioEntity> listar() { return repository.listarFuncionarios(); }
}
