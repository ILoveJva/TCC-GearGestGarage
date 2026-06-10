package model;

public class FabricantePeca {
    private long idFabricantePeca;
    private String nome;

    public FabricantePeca() {}
    public FabricantePeca(long id, String nome) { this.idFabricantePeca = id; this.nome = nome; }

    public long getIdFabricantePeca() { return idFabricantePeca; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
