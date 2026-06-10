package br.com.oficina.veiculo;

/** Detalhes tecnicos isolados do veiculo (tabela detalhes_veiculo). */
public class DetalhesVeiculoEntity {
    private Long idDetalhes;
    private Long idVeiculo;
    private String motor;
    private String cambio;
    private String direcao;
    private String sistemaFreios;
    private String cor;
    private String vin;

    public DetalhesVeiculoEntity() {}

    public Long getIdDetalhes() { return idDetalhes; }
    public void setIdDetalhes(Long id) { this.idDetalhes = id; }
    public Long getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(Long id) { this.idVeiculo = id; }
    public String getMotor() { return motor; }
    public void setMotor(String m) { this.motor = m; }
    public String getCambio() { return cambio; }
    public void setCambio(String c) { this.cambio = c; }
    public String getDirecao() { return direcao; }
    public void setDirecao(String d) { this.direcao = d; }
    public String getSistemaFreios() { return sistemaFreios; }
    public void setSistemaFreios(String s) { this.sistemaFreios = s; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
}
