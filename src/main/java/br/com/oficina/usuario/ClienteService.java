package br.com.oficina.usuario;

import br.com.oficina.usuario.dto.ClienteResponseDTO;
import java.util.ArrayList;
import java.util.List;

public class ClienteService {
    private final UsuarioRepository repository;
    public ClienteService(UsuarioRepository repository) { this.repository = repository; }

    public ClienteEntity cadastrar(String nome, String email, String senha,
                                   String telefone, long idOficina) {
        return repository.salvarCliente(nome, email, senha, telefone, idOficina);
    }

    public List<ClienteResponseDTO> listar() {
        List<ClienteResponseDTO> out = new ArrayList<>();
        for (ClienteEntity c : repository.listarClientes())
            out.add(new ClienteResponseDTO(c.getIdCliente(), c.getNome(), c.getEmail(),
                c.getTelefone(), c.getCpf() == null ? "" : c.getCpf()));
        return out;
    }

    public List<ClienteEntity> listarEntidades() { return repository.listarClientes(); }
}
