package br.com.oficina.atendimento;

import java.util.ArrayList;
import java.util.List;

public class ServicoEntity {
    private Long idServico;
    private String codigo;
    private String dataServico;
    private int kmServico;
    private String titulo;
    private String tipoServico;
    private String tipoManutencao;
    private String status;
    private Long idVeiculo;
    private Long idOficina;
    private Long idOrcamento;
    private List<ItemServicoEntity> itens = new ArrayList<>();

    public ServicoEntity() {}
    public ServicoEntity(Long id, String codigo, String dataServico, int kmServico, String titulo,
                         String tipoServico, String status, Long idVeiculo, Long idOficina, Long idOrcamento) {
        this.idServico = id; this.codigo = codigo; this.dataServico = dataServico;
        this.kmServico = kmServico; this.titulo = titulo; this.tipoServico = tipoServico;
        this.status = status; this.idVeiculo = idVeiculo; this.idOficina = idOficina;
        this.idOrcamento = idOrcamento;
    }

    public Long getIdServico() { return idServico; }
    public void setIdServico(Long id) { this.idServico = id; }
    public String getCodigo() { return codigo != null ? codigo : ""; }
    public void setCodigo(String c) { this.codigo = c; }
    public String getDataServico() { return dataServico; }
    public void setDataServico(String d) { this.dataServico = d; }
    public int getKmServico() { return kmServico; }
    public void setKmServico(int km) { this.kmServico = km; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String t) { this.titulo = t; }
    public String getTipoServico() { return tipoServico; }
    public void setTipoServico(String t) { this.tipoServico = t; }
    public String getTipoManutencao() { return tipoManutencao != null ? tipoManutencao : "CORRETIVA"; }
    public void setTipoManutencao(String m) { this.tipoManutencao = m; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public Long getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(Long id) { this.idVeiculo = id; }
    public Long getIdOficina() { return idOficina; }
    public void setIdOficina(Long id) { this.idOficina = id; }
    public Long getIdOrcamento() { return idOrcamento; }
    public void setIdOrcamento(Long id) { this.idOrcamento = id; }
    public List<ItemServicoEntity> getItens() { return itens; }
    public void setItens(List<ItemServicoEntity> i) { this.itens = i; }
}
