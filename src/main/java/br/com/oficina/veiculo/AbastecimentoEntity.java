package br.com.oficina.veiculo;

/** Abastecimento de combustível registrado pelo cliente para acompanhar o consumo do veículo. */
public class AbastecimentoEntity {
    private Long idAbastecimento;
    private Long idVeiculo;
    private String dataAbastecimento; // ISO yyyy-MM-dd
    private double valor;
    private double litros;
    private String tipoCombustivel; // GASOLINA | ETANOL | DIESEL | GNV | FLEX | OUTROS
    private int quilometragem;

    public AbastecimentoEntity() {}

    public AbastecimentoEntity(Long idAbastecimento, Long idVeiculo, String dataAbastecimento, double valor,
                               double litros, String tipoCombustivel, int quilometragem) {
        this.idAbastecimento = idAbastecimento;
        this.idVeiculo = idVeiculo;
        this.dataAbastecimento = dataAbastecimento;
        this.valor = valor;
        this.litros = litros;
        this.tipoCombustivel = tipoCombustivel != null ? tipoCombustivel : "OUTROS";
        this.quilometragem = quilometragem;
    }

    public Long getIdAbastecimento()           { return idAbastecimento; }
    public void setIdAbastecimento(Long id)    { this.idAbastecimento = id; }
    public Long getIdVeiculo()                 { return idVeiculo; }
    public void setIdVeiculo(Long id)          { this.idVeiculo = id; }
    public String getDataAbastecimento()       { return dataAbastecimento; }
    public void setDataAbastecimento(String d) { this.dataAbastecimento = d; }
    public double getValor()                   { return valor; }
    public void setValor(double v)              { this.valor = v; }
    public double getLitros()                   { return litros; }
    public void setLitros(double l)              { this.litros = l; }
    public String getTipoCombustivel()          { return tipoCombustivel != null ? tipoCombustivel : "OUTROS"; }
    public void setTipoCombustivel(String t)    { this.tipoCombustivel = t != null ? t : "OUTROS"; }
    public int getQuilometragem()                { return quilometragem; }
    public void setQuilometragem(int km)          { this.quilometragem = km; }

    public String getTipoCombustivelLabel() {
        return switch (getTipoCombustivel()) {
            case "GASOLINA" -> "Gasolina";
            case "ETANOL"   -> "Etanol";
            case "DIESEL"   -> "Diesel";
            case "GNV"      -> "GNV";
            case "FLEX"     -> "Flex";
            default         -> "Outros";
        };
    }

    /** Preço por litro (R$/L), ou 0 se os litros não foram informados. */
    public double getValorPorLitro() { return litros > 0 ? valor / litros : 0.0; }
}
