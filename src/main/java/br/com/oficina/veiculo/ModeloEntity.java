package br.com.oficina.veiculo;

public class ModeloEntity {
    private Long idModelo;
    private String nome;
    private int ano;
    private String tipo;
    private Long idMontadora;

    public ModeloEntity() {}
    public ModeloEntity(Long id, String nome, int ano, String tipo, Long idMontadora) {
        this.idModelo = id; this.nome = nome; this.ano = ano;
        this.tipo = tipo; this.idMontadora = idMontadora;
    }

    public Long getIdModelo() { return idModelo; }
    public void setIdModelo(Long id) { this.idModelo = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }
    public String getTipo() { return tipo != null ? tipo : ""; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Long getIdMontadora() { return idMontadora; }
    public void setIdMontadora(Long id) { this.idMontadora = id; }
}
