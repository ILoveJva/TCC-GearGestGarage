package br.com.oficina.estoque;

/** Uma movimentação de estoque de uma peça: ENTRADA (manual) ou SAIDA (automática por OS). */
public class MovimentacaoEstoqueEntity {
    private Long idMovimentacao;
    private Long idPeca;
    private String tipo;        // ENTRADA | SAIDA
    private int quantidade;
    private String dataMovimentacao; // ISO yyyy-MM-dd
    private String origem;      // MANUAL | OS
    private Long idServico;     // preenchido quando origem = OS
    private String observacao;
    private Double valor;       // custo de compra (apenas ENTRADA manual); null quando desconhecido

    public MovimentacaoEstoqueEntity() {}

    public MovimentacaoEstoqueEntity(Long idMovimentacao, Long idPeca, String tipo, int quantidade,
                                     String dataMovimentacao, String origem, Long idServico, String observacao,
                                     Double valor) {
        this.idMovimentacao = idMovimentacao;
        this.idPeca = idPeca;
        this.tipo = tipo != null ? tipo : "ENTRADA";
        this.quantidade = quantidade;
        this.dataMovimentacao = dataMovimentacao;
        this.origem = origem != null ? origem : "MANUAL";
        this.idServico = idServico;
        this.observacao = observacao != null ? observacao : "";
        this.valor = valor;
    }

    public Long getIdMovimentacao()          { return idMovimentacao; }
    public void setIdMovimentacao(Long id)   { this.idMovimentacao = id; }
    public Long getIdPeca()                  { return idPeca; }
    public void setIdPeca(Long id)           { this.idPeca = id; }
    public String getTipo()                  { return tipo != null ? tipo : "ENTRADA"; }
    public void setTipo(String t)            { this.tipo = t; }
    public int getQuantidade()               { return quantidade; }
    public void setQuantidade(int q)         { this.quantidade = q; }
    public String getDataMovimentacao()      { return dataMovimentacao; }
    public void setDataMovimentacao(String d){ this.dataMovimentacao = d; }
    public String getOrigem()                { return origem != null ? origem : "MANUAL"; }
    public void setOrigem(String o)          { this.origem = o; }
    public Long getIdServico()               { return idServico; }
    public void setIdServico(Long id)        { this.idServico = id; }
    public String getObservacao()            { return observacao != null ? observacao : ""; }
    public void setObservacao(String o)      { this.observacao = o; }
    public Double getValor()                 { return valor; }
    public void setValor(Double v)           { this.valor = v; }

    public boolean isEntrada() { return "ENTRADA".equalsIgnoreCase(getTipo()); }

    public String getTipoLabel()   { return isEntrada() ? "Entrada" : "Saída"; }
    public String getOrigemLabel() { return "OS".equalsIgnoreCase(getOrigem()) ? "OS" : "Manual"; }
    public String getValorFormatado() { return valor != null ? String.format("R$ %.2f", valor) : "—"; }
}
