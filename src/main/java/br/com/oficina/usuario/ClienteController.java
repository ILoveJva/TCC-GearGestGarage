package br.com.oficina.usuario;

import br.com.oficina.usuario.dto.ClienteResponseDTO;
import java.util.List;

public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }

    public ClienteEntity cadastrar(String nome, String email, String senha,
                                   String telefone, long idOficina) {
        return service.cadastrar(nome, email, senha, telefone, idOficina);
    }
    public List<ClienteResponseDTO> todos() { return service.listar(); }
    public List<ClienteEntity> entidades() { return service.listarEntidades(); }
}
