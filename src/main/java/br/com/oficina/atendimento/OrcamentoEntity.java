package br.com.oficina.atendimento;

public class OrcamentoEntity {
    private Long idOrcamento;
    private double valor;
    private String codigo;
    private String tipo;
    private String responsavel;
    private String reclamacao;
    private String dataCriacao;
    private String status;
    private Long idPeca;
    private Long idVeiculo;
    private Long idCliente;
    private Long idFuncionario;
    private Long idServicoRevisao;

    public OrcamentoEntity() {}
    public OrcamentoEntity(Long id, double valor, String codigo, String tipo, String responsavel,
                           String reclamacao, String dataCriacao, String status, Long idPeca,
                           Long idVeiculo, Long idCliente, Long idFuncionario, Long idServicoRevisao) {
        this.idOrcamento = id; this.valor = valor; this.codigo = codigo; this.tipo = tipo;
        this.responsavel = responsavel; this.reclamacao = reclamacao; this.dataCriacao = dataCriacao;
        this.status = status; this.idPeca = idPeca; this.idVeiculo = idVeiculo;
        this.idCliente = idCliente; this.idFuncionario = idFuncionario;
        this.idServicoRevisao = idServicoRevisao;
    }

    public Long getIdOrcamento() { return idOrcamento; }
    public void setIdOrcamento(Long id) { this.idOrcamento = id; }
    public double getValor() { return valor; }
    public void setValor(double v) { this.valor = v; }
    public String getCodigo() { return codigo != null ? codigo : ""; }
    public void setCodigo(String c) { this.codigo = c; }
    public String getTipo() { return tipo != null ? tipo : "ENTRADA"; }
    public void setTipo(String t) { this.tipo = t; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String r) { this.responsavel = r; }
    public String getReclamacao() { return reclamacao; }
    public void setReclamacao(String r) { this.reclamacao = r; }
    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String d) { this.dataCriacao = d; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public Long getIdPeca() { return idPeca; }
    public void setIdPeca(Long id) { this.idPeca = id; }
    public Long getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(Long id) { this.idVeiculo = id; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long id) { this.idCliente = id; }
    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long id) { this.idFuncionario = id; }
    public Long getIdServicoRevisao() { return idServicoRevisao; }
    public void setIdServicoRevisao(Long id) { this.idServicoRevisao = id; }
}
