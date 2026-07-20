package model;

import java.util.ArrayList;
import java.util.List;

public class OrdemDeServico {
    private long idOrdemDeServico;
    private String titulo;
    private Status status;
    private TipoServicoOS tipoServico;
    private TipoManutencao tipoManutencao;
    private Veiculo veiculo;
    private Funcionario responsavel;
    private final List<ItemServico> itensServico = new ArrayList<>();

    public enum Status { ABERTA, EM_ANDAMENTO, CONCLUIDA }

    public enum TipoServicoOS {
        REVISAO       ("Revisão"),
        MOTOR         ("Motor"),
        TRANSMISSAO   ("Transmissão"),
        DIRECAO       ("Direção"),
        SUSPENSAO     ("Suspensão"),
        FREIOS        ("Freios"),
        ARREFECIMENTO ("Arrefecimento"),
        ELETRICA      ("Elétrica"),
        OUTROS        ("Outros");

        private final String label;
        TipoServicoOS(String label) { this.label = label; }
        public String getLabel() { return label; }

        public static TipoServicoOS fromName(String name) {
            if (name == null) return OUTROS;
            try { return valueOf(name.toUpperCase()); }
            catch (IllegalArgumentException e) { return OUTROS; }
        }

        public static TipoServicoOS fromLabel(String label) {
            if (label == null) return OUTROS;
            for (TipoServicoOS t : values())
                if (t.label.equalsIgnoreCase(label)) return t;
            return OUTROS;
        }
    }

    public enum TipoManutencao {
        PREVENTIVA("Preventiva"),
        CORRETIVA ("Corretiva");

        private final String label;
        TipoManutencao(String label) { this.label = label; }
        public String getLabel() { return label; }

        public static TipoManutencao fromName(String name) {
            if (name == null) return CORRETIVA;
            try { return valueOf(name.toUpperCase()); }
            catch (IllegalArgumentException e) { return CORRETIVA; }
        }

        public static TipoManutencao fromLabel(String label) {
            if (label == null) return CORRETIVA;
            for (TipoManutencao m : values())
                if (m.label.equalsIgnoreCase(label)) return m;
            return CORRETIVA;
        }
    }

    public OrdemDeServico() {}
    public OrdemDeServico(long id, String titulo, Status status, Veiculo veiculo) {
        this.idOrdemDeServico = id; this.titulo = titulo;
        this.status = status; this.veiculo = veiculo;
    }

    public long getIdOrdemDeServico()               { return idOrdemDeServico; }
    public String getTitulo()                        { return titulo; }
    public void setTitulo(String titulo)             { this.titulo = titulo; }
    public Status getStatus()                        { return status; }
    public void setStatus(Status status)             { this.status = status; }
    public TipoServicoOS getTipoServico()            { return tipoServico; }
    public void setTipoServico(TipoServicoOS t)      { this.tipoServico = t; }
    public TipoManutencao getTipoManutencao()        { return tipoManutencao; }
    public void setTipoManutencao(TipoManutencao m)  { this.tipoManutencao = m; }
    public Veiculo getVeiculo()                      { return veiculo; }
    public void setVeiculo(Veiculo v)                { this.veiculo = v; }
    public Funcionario getResponsavel()              { return responsavel; }
    public void setResponsavel(Funcionario r)        { this.responsavel = r; }
    public List<ItemServico> getItensServico()       { return itensServico; }
}
