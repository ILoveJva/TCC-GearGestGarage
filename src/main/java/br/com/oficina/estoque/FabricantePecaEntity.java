package br.com.oficina.estoque;

public class FabricantePecaEntity {
    private Long idFabricantePeca;
    private String nome;
    private String pais;

    public FabricantePecaEntity() {}
    public FabricantePecaEntity(Long id, String nome, String pais) {
        this.idFabricantePeca = id; this.nome = nome; this.pais = pais;
    }
    public Long getIdFabricantePeca() { return idFabricantePeca; }
    public void setIdFabricantePeca(Long id) { this.idFabricantePeca = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}
