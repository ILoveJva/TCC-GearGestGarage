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

    public MovimentacaoEstoqueEntity() {}

    public MovimentacaoEstoqueEntity(Long idMovimentacao, Long idPeca, String tipo, int quantidade,
                                     String dataMovimentacao, String origem, Long idServico, String observacao) {
        this.idMovimentacao = idMovimentacao;
        this.idPeca = idPeca;
        this.tipo = tipo != null ? tipo : "ENTRADA";
        this.quantidade = quantidade;
        this.dataMovimentacao = dataMovimentacao;
        this.origem = origem != null ? origem : "MANUAL";
        this.idServico = idServico;
        this.observacao = observacao != null ? observacao : "";
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

    public boolean isEntrada() { return "ENTRADA".equalsIgnoreCase(getTipo()); }

    public String getTipoLabel()   { return isEntrada() ? "Entrada" : "Saída"; }
    public String getOrigemLabel() { return "OS".equalsIgnoreCase(getOrigem()) ? "OS" : "Manual"; }
}
