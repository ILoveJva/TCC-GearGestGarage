package br.com.oficina.usuario;

/** Entidade Funcionario (tabela funcionario). */
public class FuncionarioEntity {
    private Long idFuncionario;
    private String nome;
    private String cargo;
    private String endereco;
    private Long idUsuario;
    // dados do usuario (para exibicao)
    private String email;
    private String telefone;
    private String cpf;

    public FuncionarioEntity() {}
    public FuncionarioEntity(Long idFuncionario, String nome, String cargo, String endereco, Long idUsuario) {
        this.idFuncionario = idFuncionario; this.nome = nome;
        this.cargo = cargo; this.endereco = endereco; this.idUsuario = idUsuario;
    }

    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long id) { this.idFuncionario = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getEndereco() { return endereco == null ? "" : endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long id) { this.idUsuario = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
