package br.com.oficina.financeiro;

/** Uma despesa (gasto) da oficina. Valores de código em maiúsculas; use os *Label() para exibição. */
public class DespesaEntity {
    private Long idDespesa;
    private String descricao;
    private String categoria;       // PECAS | SALARIO | ALUGUEL | FERRAMENTAS | IMPOSTOS | CONTAS | OUTROS
    private double valor;
    private String dataDespesa;     // ISO yyyy-MM-dd
    private String formaPagamento;  // DINHEIRO | PIX | CARTAO_CREDITO | CARTAO_DEBITO | BOLETO | TRANSFERENCIA | OUTROS
    private String observacao;
    private Long idOficina;

    public DespesaEntity() {}

    public DespesaEntity(Long idDespesa, String descricao, String categoria, double valor,
                         String dataDespesa, String formaPagamento, String observacao, Long idOficina) {
        this.idDespesa = idDespesa;
        this.descricao = descricao;
        this.categoria = categoria != null ? categoria : "OUTROS";
        this.valor = valor;
        this.dataDespesa = dataDespesa;
        this.formaPagamento = formaPagamento != null ? formaPagamento : "OUTROS";
        this.observacao = observacao != null ? observacao : "";
        this.idOficina = idOficina;
    }

    public Long getIdDespesa()               { return idDespesa; }
    public void setIdDespesa(Long id)        { this.idDespesa = id; }
    public String getDescricao()             { return descricao; }
    public void setDescricao(String d)       { this.descricao = d; }
    public String getCategoria()             { return categoria != null ? categoria : "OUTROS"; }
    public void setCategoria(String c)       { this.categoria = c != null ? c : "OUTROS"; }
    public double getValor()                 { return valor; }
    public void setValor(double v)           { this.valor = v; }
    public String getDataDespesa()           { return dataDespesa; }
    public void setDataDespesa(String d)     { this.dataDespesa = d; }
    public String getFormaPagamento()        { return formaPagamento != null ? formaPagamento : "OUTROS"; }
    public void setFormaPagamento(String f)  { this.formaPagamento = f != null ? f : "OUTROS"; }
    public String getObservacao()            { return observacao != null ? observacao : ""; }
    public void setObservacao(String o)      { this.observacao = o != null ? o : ""; }
    public Long getIdOficina()               { return idOficina; }
    public void setIdOficina(Long id)        { this.idOficina = id; }

    public String getCategoriaLabel() {
        return switch (getCategoria()) {
            case "PECAS"       -> "Peças";
            case "SALARIO"     -> "Salário";
            case "ALUGUEL"     -> "Aluguel";
            case "FERRAMENTAS" -> "Ferramentas";
            case "IMPOSTOS"    -> "Impostos";
            case "CONTAS"      -> "Contas";
            default            -> "Outros";
        };
    }

    public String getFormaPagamentoLabel() {
        return switch (getFormaPagamento()) {
            case "DINHEIRO"       -> "Dinheiro";
            case "PIX"            -> "Pix";
            case "CARTAO_CREDITO" -> "Cartão de Crédito";
            case "CARTAO_DEBITO"  -> "Cartão de Débito";
            case "BOLETO"         -> "Boleto";
            case "TRANSFERENCIA"  -> "Transferência";
            default               -> "Outros";
        };
    }
}
