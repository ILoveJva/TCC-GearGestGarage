package model;

import java.util.ArrayList;
import java.util.List;

/** Model rico de Montadora (camada de apresentacao). */
public class Montadora {
    private long idMontadora;
    private String nome;
    private String paisOrigem;
    private final List<Modelo> modelos = new ArrayList<>();

    public Montadora() {}
    public Montadora(long idMontadora, String nome, String paisOrigem) {
        this.idMontadora = idMontadora; this.nome = nome; this.paisOrigem = paisOrigem;
    }

    public List<Modelo> listarModelos() { return modelos; }
    public void addModelo(Modelo m) { modelos.add(m); }

    public long getIdMontadora() { return idMontadora; }
    public void setIdMontadora(long id) { this.idMontadora = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPaisOrigem() { return paisOrigem; }
    public void setPaisOrigem(String p) { this.paisOrigem = p; }

    @Override public String toString() { return nome; }
}
