package br.com.oficina.estoque;

public class PecaEntity {
    private Long idPeca;
    private String nomePeca;
    private String vidaUtilTempo;
    private String vidaUtilKm;
    private Long idFabricantePeca;
    private Long idVeiculo;

    public PecaEntity() {}
    public PecaEntity(Long id, String nomePeca, String vidaUtilTempo, String vidaUtilKm,
                      Long idFabricantePeca, Long idVeiculo) {
        this.idPeca = id; this.nomePeca = nomePeca; this.vidaUtilTempo = vidaUtilTempo;
        this.vidaUtilKm = vidaUtilKm; this.idFabricantePeca = idFabricantePeca; this.idVeiculo = idVeiculo;
    }
    public Long getIdPeca() { return idPeca; }
    public void setIdPeca(Long id) { this.idPeca = id; }
    public String getNomePeca() { return nomePeca; }
    public void setNomePeca(String n) { this.nomePeca = n; }
    public String getVidaUtilTempo() { return vidaUtilTempo; }
    public void setVidaUtilTempo(String v) { this.vidaUtilTempo = v; }
    public String getVidaUtilKm() { return vidaUtilKm; }
    public void setVidaUtilKm(String v) { this.vidaUtilKm = v; }
    public Long getIdFabricantePeca() { return idFabricantePeca; }
    public void setIdFabricantePeca(Long id) { this.idFabricantePeca = id; }
    public Long getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(Long id) { this.idVeiculo = id; }
}
