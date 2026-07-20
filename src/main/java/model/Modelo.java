package model;

public class Modelo {
    private long idModelo;
    private String nome;
    private int ano;
    private String tipo;
    private Montadora montadora;

    public Modelo() {}
    public Modelo(long idModelo, String nome, int ano, String tipo, Montadora montadora) {
        this.idModelo = idModelo; this.nome = nome; this.ano = ano;
        this.tipo = tipo; this.montadora = montadora;
    }

    public long getIdModelo() { return idModelo; }
    public void setIdModelo(long id) { this.idModelo = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    public String getTipo() { return tipo != null ? tipo : ""; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Montadora getMontadora() { return montadora; }
    public void setMontadora(Montadora m) { this.montadora = m; }

    @Override public String toString() { return nome; }
}
