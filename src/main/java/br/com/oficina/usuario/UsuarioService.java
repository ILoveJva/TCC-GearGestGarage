package br.com.oficina.usuario;

import br.com.oficina.shared.config.SecurityConfig;
import br.com.oficina.usuario.dto.LoginRequestDTO;
import java.util.List;

public class UsuarioService {
    private final UsuarioRepository repository;
    public UsuarioService(UsuarioRepository repository) { this.repository = repository; }

    public UsuarioEntity login(LoginRequestDTO dados) {
        UsuarioEntity u = repository.buscarPorEmail(dados.email());
        if (u == null) return null;
        return SecurityConfig.senhaConfere(dados.senha(), u.getSenha()) ? u : null;
    }

    public void logout(long idUsuario) { /* sessao nao persistida nesta camada */ }
    public List<UsuarioEntity> listarTodos() { return repository.listarUsuarios(); }
}
