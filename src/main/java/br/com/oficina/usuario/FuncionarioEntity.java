package br.com.oficina.usuario;

/** Entidade Funcionario (tabela funcionario). */
public class FuncionarioEntity {
    private Long idFuncionario;
    private String nome;
    private String cargo;
    private Long idUsuario;

    public FuncionarioEntity() {}
    public FuncionarioEntity(Long idFuncionario, String nome, String cargo, Long idUsuario) {
        this.idFuncionario = idFuncionario; this.nome = nome;
        this.cargo = cargo; this.idUsuario = idUsuario;
    }

    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long id) { this.idFuncionario = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long id) { this.idUsuario = id; }
}
