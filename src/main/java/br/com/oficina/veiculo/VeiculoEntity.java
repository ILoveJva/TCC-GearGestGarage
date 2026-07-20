package br.com.oficina.veiculo;

public class VeiculoEntity {
    private Long idVeiculo;
    private String placa;
    private String codigo;
    private Long idCliente;
    private Long idModelo;

    public VeiculoEntity() {}
    public VeiculoEntity(Long id, String placa, String codigo, Long idCliente, Long idModelo) {
        this.idVeiculo = id; this.placa = placa; this.codigo = codigo;
        this.idCliente = idCliente; this.idModelo = idModelo;
    }

    public Long getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(Long id) { this.idVeiculo = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getCodigo() { return codigo != null ? codigo : ""; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long id) { this.idCliente = id; }
    public Long getIdModelo() { return idModelo; }
    public void setIdModelo(Long id) { this.idModelo = id; }
}
