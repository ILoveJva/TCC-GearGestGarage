package br.com.oficina.usuario;

import java.util.List;

public class FuncionarioService {
    private final UsuarioRepository repository;
    public FuncionarioService(UsuarioRepository repository) { this.repository = repository; }

    public FuncionarioEntity cadastrar(String nome, String cargo, String endereco, String cpf,
                                       String email, String senha, String telefone, long idOficina) {
        return repository.salvarFuncionario(nome, cargo, endereco, cpf, email, senha, telefone, idOficina);
    }

    public List<FuncionarioEntity> listar() { return repository.listarFuncionarios(); }

    public boolean atualizar(long idFuncionario, String nome, String cargo, String endereco,
                             String cpf, String email, String telefone) {
        return repository.atualizarFuncionario(idFuncionario, nome, cargo, endereco, cpf, email, telefone);
    }

    public void remover(long idFuncionario) { repository.removerFuncionario(idFuncionario); }
}
