package br.com.oficina.usuario;

/** Entidade Cliente (tabela cliente). Carrega tambem os dados do usuario associado. */
public class ClienteEntity {
    private Long idCliente;
    private Long idUsuario;
    // dados do usuario (para exibicao)
    private String nome;
    private String email;
    private String telefone;
    private String cpf; // nao existe no schema; mantido vazio para compatibilidade de view

    public ClienteEntity() {}

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long id) { this.idCliente = id; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long id) { this.idUsuario = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String t) { this.telefone = t; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
