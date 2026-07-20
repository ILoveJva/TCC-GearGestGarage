package br.com.oficina.atendimento;

public class ItemServicoEntity {
    private Long idItemServico;
    private int etapa;
    private String codigo;
    private String descricao;
    private String status;
    private String tempoGasto;
    private String dataRealizacao;
    private Long idPeca;
    private Long idServico;
    private Long idFuncionario;

    public ItemServicoEntity() {}
    public ItemServicoEntity(Long id, int etapa, String codigo, String descricao, String status,
                             String tempoGasto, String dataRealizacao,
                             Long idPeca, Long idServico, Long idFuncionario) {
        this.idItemServico = id; this.etapa = etapa; this.codigo = codigo;
        this.descricao = descricao; this.status = status; this.tempoGasto = tempoGasto != null ? tempoGasto : "";
        this.dataRealizacao = dataRealizacao;
        this.idPeca = idPeca; this.idServico = idServico; this.idFuncionario = idFuncionario;
    }

    public Long getIdItemServico() { return idItemServico; }
    public void setIdItemServico(Long id) { this.idItemServico = id; }
    public int getEtapa() { return etapa; }
    public void setEtapa(int e) { this.etapa = e; }
    public String getCodigo() { return codigo != null ? codigo : ""; }
    public void setCodigo(String c) { this.codigo = c; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String d) { this.descricao = d; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public String getTempoGasto() { return tempoGasto != null ? tempoGasto : ""; }
    public void setTempoGasto(String t) { this.tempoGasto = t != null ? t : ""; }
    public String getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(String d) { this.dataRealizacao = d; }
    public Long getIdPeca() { return idPeca; }
    public void setIdPeca(Long id) { this.idPeca = id; }
    public Long getIdServico() { return idServico; }
    public void setIdServico(Long id) { this.idServico = id; }
    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long id) { this.idFuncionario = id; }
}
