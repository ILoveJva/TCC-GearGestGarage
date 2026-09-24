package br.com.oficina.estoque;

/** Peça real (SKU): instância comprada de um item do catálogo, com fabricante, nome técnico e estoque próprios. */
public class PecaEntity {
    private Long idPeca;
    private Long idCatalogoPeca;
    private String nomeTecnico;
    private String fabricante;
    private double valor;
    private int quantidadeEstoque;

    public PecaEntity() {}
    public PecaEntity(Long idPeca, Long idCatalogoPeca, String nomeTecnico, String fabricante, double valor) {
        this.idPeca = idPeca;
        this.idCatalogoPeca = idCatalogoPeca;
        this.nomeTecnico = nomeTecnico != null ? nomeTecnico : "";
        this.fabricante = fabricante != null ? fabricante : "";
        this.valor = valor;
    }

    public Long getIdPeca() { return idPeca; }
    public void setIdPeca(Long id) { this.idPeca = id; }
    public Long getIdCatalogoPeca() { return idCatalogoPeca; }
    public void setIdCatalogoPeca(Long id) { this.idCatalogoPeca = id; }
    public String getNomeTecnico() { return nomeTecnico; }
    public void setNomeTecnico(String n) { this.nomeTecnico = n != null ? n : ""; }
    public String getFabricante() { return fabricante; }
    public void setFabricante(String f) { this.fabricante = f != null ? f : ""; }
    public double getValor() { return valor; }
    public void setValor(double v) { this.valor = v; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int q) { this.quantidadeEstoque = q; }

    /** Nome de exibição: nome técnico, com um fallback legível quando não informado. */
    public String getNomeExibicao() {
        return (nomeTecnico != null && !nomeTecnico.isBlank()) ? nomeTecnico : "(sem nome técnico)";
    }
}
