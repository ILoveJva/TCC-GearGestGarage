package br.com.oficina.estoque;

public class CatalogoPecaEntity {
    private Long idCatalogoPeca;
    private String nomePopular;
    private String vidaUtilTempo;
    private String vidaUtilKm;
    private String sistema; // MOTOR | TRANSMISSAO | DIRECAO | SUSPENSAO | FREIOS | ARREFECIMENTO | ELETRICA | ALIMENTACAO | OUTROS

    public CatalogoPecaEntity() {}
    public CatalogoPecaEntity(Long id, String nomePopular, String vidaUtilTempo, String vidaUtilKm, String sistema) {
        this.idCatalogoPeca = id; this.nomePopular = nomePopular;
        this.vidaUtilTempo = vidaUtilTempo;
        this.vidaUtilKm = vidaUtilKm;
        this.sistema = sistema != null ? sistema : "OUTROS";
    }
    public Long getIdCatalogoPeca() { return idCatalogoPeca; }
    public void setIdCatalogoPeca(Long id) { this.idCatalogoPeca = id; }
    public String getNomePopular() { return nomePopular; }
    public void setNomePopular(String n) { this.nomePopular = n; }
    public String getNomeExibicao() { return nomePopular; }
    public String getVidaUtilTempo() { return vidaUtilTempo; }
    public void setVidaUtilTempo(String v) { this.vidaUtilTempo = v; }
    public String getVidaUtilKm() { return vidaUtilKm; }
    public void setVidaUtilKm(String v) { this.vidaUtilKm = v; }
    public String getSistema() { return sistema != null ? sistema : "OUTROS"; }
    public void setSistema(String s) { this.sistema = s != null ? s : "OUTROS"; }

    public String getSistemaLabel() {
        return switch (getSistema()) {
            case "MOTOR"         -> "Motor";
            case "TRANSMISSAO"   -> "Transmissão";
            case "DIRECAO"       -> "Direção";
            case "SUSPENSAO"     -> "Suspensão";
            case "FREIOS"        -> "Freios";
            case "ARREFECIMENTO" -> "Arrefecimento";
            case "ELETRICA"      -> "Elétrica";
            case "ALIMENTACAO"   -> "Alimentação";
            default              -> "Outros";
        };
    }
}
