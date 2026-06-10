package br.com.oficina.oficina;

public class OficinaEntity {
    private Long idOficina;
    private String nome;
    private String endereco;
    private String telefone;
    private String cnpj;

    public OficinaEntity() {}
    public OficinaEntity(Long id, String nome, String endereco, String telefone, String cnpj) {
        this.idOficina = id; this.nome = nome; this.endereco = endereco;
        this.telefone = telefone; this.cnpj = cnpj;
    }
    public Long getIdOficina() { return idOficina; }
    public void setIdOficina(Long id) { this.idOficina = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String e) { this.endereco = e; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String t) { this.telefone = t; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String c) { this.cnpj = c; }
}
