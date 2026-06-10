package br.com.oficina.usuario;

/** Entidade base de acesso (tabela usuario). Cliente e Funcionario referenciam um usuario. */
public class UsuarioEntity {
    private Long idUsuario;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private Long idOficina;

    public UsuarioEntity() {}
    public UsuarioEntity(Long idUsuario, String nome, String email, String senha,
                         String telefone, Long idOficina) {
        this.idUsuario = idUsuario; this.nome = nome; this.email = email;
        this.senha = senha; this.telefone = telefone; this.idOficina = idOficina;
    }

    public boolean verificarEmail() { return email != null && email.contains("@"); }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long id) { this.idUsuario = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String t) { this.telefone = t; }
    public Long getIdOficina() { return idOficina; }
    public void setIdOficina(Long id) { this.idOficina = id; }
}
