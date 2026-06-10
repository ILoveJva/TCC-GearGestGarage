package br.com.oficina.veiculo;

public class MontadoraEntity {
    private Long idMontadora;
    private String nome;
    private String paisOrigem;

    public MontadoraEntity() {}
    public MontadoraEntity(Long id, String nome, String paisOrigem) {
        this.idMontadora = id; this.nome = nome; this.paisOrigem = paisOrigem;
    }
    public Long getIdMontadora() { return idMontadora; }
    public void setIdMontadora(Long id) { this.idMontadora = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPaisOrigem() { return paisOrigem; }
    public void setPaisOrigem(String p) { this.paisOrigem = p; }
}
