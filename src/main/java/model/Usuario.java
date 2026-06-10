package model;

/** Classe base de usuario (heranca do diagrama). */
public abstract class Usuario {
    protected long idUsuario;
    protected String nome;
    protected String email;
    protected String senha;
    protected String telefone;
    protected String cpf;

    public long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(long id) { this.idUsuario = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String t) { this.telefone = t; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
