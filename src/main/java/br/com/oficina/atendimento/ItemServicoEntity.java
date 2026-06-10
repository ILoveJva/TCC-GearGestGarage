package br.com.oficina.atendimento;

public class ItemServicoEntity {
    private Long idItemServico;
    private String descricao;
    private String status;
    private String dataRealizacao;
    private Long idPeca;
    private Long idServico;
    private Long idFuncionario;

    public ItemServicoEntity() {}
    public ItemServicoEntity(Long id, String descricao, String status, String dataRealizacao,
                             Long idPeca, Long idServico, Long idFuncionario) {
        this.idItemServico = id; this.descricao = descricao; this.status = status;
        this.dataRealizacao = dataRealizacao; this.idPeca = idPeca;
        this.idServico = idServico; this.idFuncionario = idFuncionario;
    }
    public Long getIdItemServico() { return idItemServico; }
    public void setIdItemServico(Long id) { this.idItemServico = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String d) { this.descricao = d; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public String getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(String d) { this.dataRealizacao = d; }
    public Long getIdPeca() { return idPeca; }
    public void setIdPeca(Long id) { this.idPeca = id; }
    public Long getIdServico() { return idServico; }
    public void setIdServico(Long id) { this.idServico = id; }
    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long id) { this.idFuncionario = id; }
}
