package br.com.oficina.usuario;

import java.util.List;

public class FuncionarioController {
    private final FuncionarioService service;
    public FuncionarioController(FuncionarioService service) { this.service = service; }

    public FuncionarioEntity cadastrar(String nome, String cargo, String endereco, String cpf,
                                       String email, String senha, String telefone, long idOficina) {
        return service.cadastrar(nome, cargo, endereco, cpf, email, senha, telefone, idOficina);
    }
    public List<FuncionarioEntity> todos() { return service.listar(); }

    public boolean atualizar(long idFuncionario, String nome, String cargo, String endereco,
                             String cpf, String email, String telefone) {
        return service.atualizar(idFuncionario, nome, cargo, endereco, cpf, email, telefone);
    }

    public void remover(long idFuncionario) { service.remover(idFuncionario); }
}
