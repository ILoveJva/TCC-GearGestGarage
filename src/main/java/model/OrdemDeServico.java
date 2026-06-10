package model;

import java.util.ArrayList;
import java.util.List;

public class OrdemDeServico {
    private long idOrdemDeServico;
    private String titulo;
    private Status status;
    private Veiculo veiculo;
    private Funcionario responsavel;
    private final List<ItemServico> itensServico = new ArrayList<>();

    public enum Status { ABERTA, EM_ANDAMENTO, CONCLUIDA }

    public OrdemDeServico() {}
    public OrdemDeServico(long id, String titulo, Status status, Veiculo veiculo) {
        this.idOrdemDeServico = id; this.titulo = titulo; this.status = status; this.veiculo = veiculo;
    }

    public long getIdOrdemDeServico() { return idOrdemDeServico; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo v) { this.veiculo = v; }
    public Funcionario getResponsavel() { return responsavel; }
    public void setResponsavel(Funcionario r) { this.responsavel = r; }
    public List<ItemServico> getItensServico() { return itensServico; }
}
