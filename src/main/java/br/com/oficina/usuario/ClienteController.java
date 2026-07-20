package br.com.oficina.usuario;

import br.com.oficina.usuario.dto.ClienteResponseDTO;
import java.util.List;

public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }

    public ClienteEntity cadastrar(String nome, String cpf, String email, String senha,
                                   String telefone, long idOficina) {
        return service.cadastrar(nome, cpf, email, senha, telefone, idOficina);
    }
    public List<ClienteResponseDTO> todos() { return service.listar(); }
    public List<ClienteEntity> entidades() { return service.listarEntidades(); }

    public boolean atualizar(long idCliente, String nome, String cpf, String email, String telefone) {
        return service.atualizar(idCliente, nome, cpf, email, telefone);
    }

    public void remover(long idCliente) { service.remover(idCliente); }
}
