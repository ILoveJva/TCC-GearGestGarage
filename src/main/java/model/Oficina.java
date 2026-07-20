package model;

public class Oficina {
    private long idOficina;
    private String nome;
    private String cnpj;
    private String descricao;
    private String email;
    private String senha;
    private String endereco = "";
    private String telefone = "";

    public Oficina() {}
    public Oficina(long idOficina, String nome, String cnpj, String descricao, String email, String senha) {
        this.idOficina = idOficina; this.nome = nome; this.cnpj = cnpj;
        this.descricao = descricao; this.email = email; this.senha = senha;
    }

    public long getIdOficina()    { return idOficina; }
    public String getNome()       { return nome; }
    public String getCnpj()       { return cnpj; }
    public String getDescricao()  { return descricao; }
    public String getEmail()      { return email; }
    public String getSenha()      { return senha; }
    public String getEndereco()   { return endereco; }
    public void   setEndereco(String e) { this.endereco = e; }
    public String getTelefone()   { return telefone; }
    public void   setTelefone(String t) { this.telefone = t; }
}
