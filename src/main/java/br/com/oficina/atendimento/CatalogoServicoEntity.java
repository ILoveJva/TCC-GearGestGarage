package br.com.oficina.atendimento;

public class CatalogoServicoEntity {
    private Long idCatalogoServico;
    private String nome;
    private String descricao;
    private double valor;
    private String tipo;
    private String sistema; // MOTOR | TRANSMISSAO | DIRECAO | SUSPENSAO | FREIOS | ARREFECIMENTO | ELETRICA | ALIMENTACAO | OUTROS
    private Integer validadeKm;
    private Integer validadeMeses;

    @Override
    public String toString() {
        return "CatalogoServicoEntity{sistema='" + sistema + "'}";
    }

    public CatalogoServicoEntity() {}
    public CatalogoServicoEntity(Long id, String nome, String descricao, double valor,
                                  String tipo, String sistema,
                                  Integer validadeKm, Integer validadeMeses) {
        this.idCatalogoServico = id; this.nome = nome; this.descricao = descricao;
        this.valor = valor; this.tipo = tipo;
        this.sistema = sistema != null ? sistema : "OUTROS";
        this.validadeKm = validadeKm; this.validadeMeses = validadeMeses;
    }

    public Long getIdCatalogoServico() { return idCatalogoServico; }
    public void setIdCatalogoServico(Long id) { this.idCatalogoServico = id; }
    public String getNome() { return nome; }
    public void setNome(String n) { this.nome = n; }
    public String getDescricao() { return descricao != null ? descricao : ""; }
    public void setDescricao(String d) { this.descricao = d; }
    public double getValor() { return valor; }
    public void setValor(double v) { this.valor = v; }
    public String getTipo() { return tipo != null ? tipo : "PADRAO"; }
    public void setTipo(String t) { this.tipo = t; }
    public String getSistema() { return sistema != null ? sistema : "OUTROS"; }
    public void setSistema(String s) { this.sistema = s; }
    public Integer getValidadeKm() { return validadeKm; }
    public void setValidadeKm(Integer v) { this.validadeKm = v; }
    public Integer getValidadeMeses() { return validadeMeses; }
    public void setValidadeMeses(Integer v) { this.validadeMeses = v; }

    public boolean isRevisao() { return "REVISAO".equalsIgnoreCase(tipo); }

    public String getSistemaLabel() {
        if (sistema == null) return "Outros";
        return switch (sistema) {
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
