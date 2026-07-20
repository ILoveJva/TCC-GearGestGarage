package br.com.oficina.atendimento;

public class TipoServicoEntity {
    private Long idTipoServico;
    private String nome;

    public TipoServicoEntity() {}
    public TipoServicoEntity(Long idTipoServico, String nome) {
        this.idTipoServico = idTipoServico;
        this.nome = nome;
    }

    public Long getIdTipoServico() { return idTipoServico; }
    public void setIdTipoServico(Long id) { this.idTipoServico = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public String toString() { return nome != null ? nome : ""; }
}
