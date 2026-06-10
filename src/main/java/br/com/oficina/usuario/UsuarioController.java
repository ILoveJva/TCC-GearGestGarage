package br.com.oficina.usuario;

import br.com.oficina.usuario.dto.LoginRequestDTO;
import java.util.List;

public class UsuarioController {
    private final UsuarioService service;
    public UsuarioController(UsuarioService service) { this.service = service; }

    public UsuarioEntity login(String email, String senha) {
        return service.login(new LoginRequestDTO(email, senha));
    }
    public void logout(long idUsuario) { service.logout(idUsuario); }
    public List<UsuarioEntity> todos() { return service.listarTodos(); }
}
