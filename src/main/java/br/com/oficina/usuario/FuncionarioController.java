package br.com.oficina.usuario;

import java.util.List;

public class FuncionarioController {
    private final FuncionarioService service;
    public FuncionarioController(FuncionarioService service) { this.service = service; }

    public FuncionarioEntity cadastrar(String nome, String cargo, String email,
                                       String senha, String telefone, long idOficina) {
        return service.cadastrar(nome, cargo, email, senha, telefone, idOficina);
    }
    public List<FuncionarioEntity> todos() { return service.listar(); }
}
