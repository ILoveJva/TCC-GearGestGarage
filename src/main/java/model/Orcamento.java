package model;

public class Orcamento {
    private final long idOrcamento;
    private final String codigo;
    private final String tipo;
    private final double valor;
    private final String responsavel;
    private final String reclamacao;
    private final String dataCriacao;
    private final String status;
    private final long idVeiculo;
    private final long idCliente;
    private final String placaVeiculo;
    private final String nomeCliente;
    private long idOficina;
    private String nomeOficina = "";
    private boolean levar;
    private boolean buscar;

    public Orcamento(long idOrcamento, String codigo, String tipo, double valor, String responsavel,
                     String reclamacao, String dataCriacao, String status, long idVeiculo, long idCliente,
                     String placaVeiculo, String nomeCliente) {
        this.idOrcamento = idOrcamento; this.codigo = codigo; this.tipo = tipo;
        this.valor = valor; this.responsavel = responsavel; this.reclamacao = reclamacao;
        this.dataCriacao = dataCriacao; this.status = status;
        this.idVeiculo = idVeiculo; this.idCliente = idCliente;
        this.placaVeiculo = placaVeiculo; this.nomeCliente = nomeCliente;
    }

    public long getIdOrcamento() { return idOrcamento; }
    public String getCodigo() { return codigo != null ? codigo : ""; }
    public String getTipo() { return tipo != null ? tipo : "ENTRADA"; }
    public double getValor() { return valor; }
    public String getResponsavel() { return responsavel; }
    public String getReclamacao() { return reclamacao; }
    public String getDataCriacao() { return dataCriacao; }
    public String getStatus() { return status; }
    public long getIdVeiculo() { return idVeiculo; }
    public long getIdCliente() { return idCliente; }
    public String getPlacaVeiculo() { return placaVeiculo; }
    public String getNomeCliente() { return nomeCliente; }
    public long getIdOficina() { return idOficina; }
    public void setIdOficina(long idOficina) { this.idOficina = idOficina; }
    public String getNomeOficina() { return nomeOficina; }
    public void setNomeOficina(String nomeOficina) { this.nomeOficina = nomeOficina; }
    public boolean isLevar() { return levar; }
    public void setLevar(boolean levar) { this.levar = levar; }
    public boolean isBuscar() { return buscar; }
    public void setBuscar(boolean buscar) { this.buscar = buscar; }


    @Override
    public String toString() {
        return "Orcamento{" +
                "idOrcamento=" + idOrcamento +
                ", codigo='" + codigo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", valor=" + valor +
                ", responsavel='" + responsavel + '\'' +
                ", reclamacao='" + reclamacao + '\'' +
                ", dataCriacao='" + dataCriacao + '\'' +
                ", status='" + status + '\'' +
                ", idVeiculo=" + idVeiculo +
                ", idCliente=" + idCliente +
                ", placaVeiculo='" + placaVeiculo + '\'' +
                ", nomeCliente='" + nomeCliente + '\'' +
                '}';
    }
}
