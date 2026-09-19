package br.com.oficina.atendimento;

/**
 * Foto ou vídeo anexado a uma ordem de serviço para o cliente acompanhar o
 * andamento. O arquivo em si fica no disco (pasta midias_os); esta entidade
 * guarda apenas os metadados e o caminho relativo.
 */
public class MidiaServicoEntity {
    private Long idMidia;
    private Long idServico;
    private String tipo;          // FOTO | VIDEO
    private String nomeArquivo;   // nome original escolhido pelo usuário
    private String caminho;       // caminho relativo do arquivo salvo em disco
    private String descricao;
    private String dataUpload;

    public MidiaServicoEntity() {}
    public MidiaServicoEntity(Long idMidia, Long idServico, String tipo, String nomeArquivo,
                              String caminho, String descricao, String dataUpload) {
        this.idMidia = idMidia; this.idServico = idServico; this.tipo = tipo;
        this.nomeArquivo = nomeArquivo; this.caminho = caminho;
        this.descricao = descricao; this.dataUpload = dataUpload;
    }

    public Long getIdMidia() { return idMidia; }
    public void setIdMidia(Long id) { this.idMidia = id; }
    public Long getIdServico() { return idServico; }
    public void setIdServico(Long id) { this.idServico = id; }
    public String getTipo() { return tipo != null ? tipo : "FOTO"; }
    public void setTipo(String t) { this.tipo = t; }
    public String getNomeArquivo() { return nomeArquivo != null ? nomeArquivo : ""; }
    public void setNomeArquivo(String n) { this.nomeArquivo = n; }
    public String getCaminho() { return caminho != null ? caminho : ""; }
    public void setCaminho(String c) { this.caminho = c; }
    public String getDescricao() { return descricao != null ? descricao : ""; }
    public void setDescricao(String d) { this.descricao = d; }
    public String getDataUpload() { return dataUpload; }
    public void setDataUpload(String d) { this.dataUpload = d; }

    public boolean isVideo() { return "VIDEO".equalsIgnoreCase(getTipo()); }
}
