package model;

public class Peca {
    private long idPeca;
    private String nome;
    private String sistema;
    private double valor;
    private int vidaUtilKm;
    private int vidaUtilTempo;
    private FabricantePeca fabricantePeca;

    public Peca() {}
    public Peca(long idPeca, String nome, String sistema, double valor,
                int vidaUtilKm, int vidaUtilTempo, FabricantePeca fabricantePeca) {
        this.idPeca = idPeca; this.nome = nome; this.sistema = sistema; this.valor = valor;
        this.vidaUtilKm = vidaUtilKm; this.vidaUtilTempo = vidaUtilTempo; this.fabricantePeca = fabricantePeca;
    }

    public long getIdPeca() { return idPeca; }
    public String getNome() { return nome; }
    public String getSistema() { return sistema; }
    public double getValor() { return valor; }
    public int getVidaUtilKm() { return vidaUtilKm; }
    public int getVidaUtilTempo() { return vidaUtilTempo; }
    public FabricantePeca getFabricantePeca() { return fabricantePeca; }
}
