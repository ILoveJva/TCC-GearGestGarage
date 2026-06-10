package br.com.oficina.atendimento;

public class OrcamentoEntity {
    private Long idOrcamento;
    private double valor;
    private Long idPeca;
    private Long idVeiculo;
    private Long idCliente;
    private Long idFuncionario;

    public OrcamentoEntity() {}
    public OrcamentoEntity(Long id, double valor, Long idPeca, Long idVeiculo, Long idCliente, Long idFuncionario) {
        this.idOrcamento = id; this.valor = valor; this.idPeca = idPeca;
        this.idVeiculo = idVeiculo; this.idCliente = idCliente; this.idFuncionario = idFuncionario;
    }
    public Long getIdOrcamento() { return idOrcamento; }
    public void setIdOrcamento(Long id) { this.idOrcamento = id; }
    public double getValor() { return valor; }
    public void setValor(double v) { this.valor = v; }
    public Long getIdPeca() { return idPeca; }
    public void setIdPeca(Long id) { this.idPeca = id; }
    public Long getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(Long id) { this.idVeiculo = id; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long id) { this.idCliente = id; }
    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long id) { this.idFuncionario = id; }
}
