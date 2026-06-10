package model;

import java.util.ArrayList;
import java.util.List;

public class Veiculo {
    private long idVeiculo;
    private String tipo;
    private String cor;
    private String placa;
    private String vin;
    private Modelo modelo;
    private final List<Peca> listaPecas = new ArrayList<>();

    public Veiculo() {}
    public Veiculo(long idVeiculo, String tipo, String cor, String placa, String vin, Modelo modelo) {
        this.idVeiculo = idVeiculo; this.tipo = tipo; this.cor = cor;
        this.placa = placa; this.vin = vin; this.modelo = modelo;
    }

    public long getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(long id) { this.idVeiculo = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public Modelo getModelo() { return modelo; }
    public void setModelo(Modelo modelo) { this.modelo = modelo; }
    public List<Peca> getListaPecas() { return listaPecas; }
}
