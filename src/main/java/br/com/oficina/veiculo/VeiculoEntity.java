package br.com.oficina.veiculo;

public class VeiculoEntity {
    private Long idVeiculo;
    private String placa;
    private String tipoVeiculo;
    private Long idCliente;
    private Long idModelo;

    public VeiculoEntity() {}
    public VeiculoEntity(Long id, String placa, String tipoVeiculo, Long idCliente, Long idModelo) {
        this.idVeiculo = id; this.placa = placa; this.tipoVeiculo = tipoVeiculo;
        this.idCliente = idCliente; this.idModelo = idModelo;
    }
    public Long getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(Long id) { this.idVeiculo = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getTipoVeiculo() { return tipoVeiculo; }
    public void setTipoVeiculo(String t) { this.tipoVeiculo = t; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long id) { this.idCliente = id; }
    public Long getIdModelo() { return idModelo; }
    public void setIdModelo(Long id) { this.idModelo = id; }
}
