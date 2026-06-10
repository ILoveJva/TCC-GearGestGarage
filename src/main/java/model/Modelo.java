package model;

/** Model rico de Modelo. Carrega o ano (usado pela tela de veiculos). */
public class Modelo {
    private long idModelo;
    private String nome;
    private int ano;
    private Montadora montadora;

    public Modelo() {}
    public Modelo(long idModelo, String nome, int ano, Montadora montadora) {
        this.idModelo = idModelo; this.nome = nome; this.ano = ano; this.montadora = montadora;
    }

    public long getIdModelo() { return idModelo; }
    public void setIdModelo(long id) { this.idModelo = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    public Montadora getMontadora() { return montadora; }
    public void setMontadora(Montadora m) { this.montadora = m; }

    @Override public String toString() { return nome; }
}
