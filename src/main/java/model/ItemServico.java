package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemServico {
    private long idItemServico;
    private Status status;
    private Date dataRealizacao;
    private String descricao;
    private Funcionario responsavel;
    private final List<Peca> listaPecas = new ArrayList<>();

    public enum Status { PENDENTE, EM_ANDAMENTO, CONCLUIDO }

    public ItemServico() {}
    public ItemServico(long id, Status status, Date dataRealizacao, String descricao) {
        this.idItemServico = id; this.status = status;
        this.dataRealizacao = dataRealizacao; this.descricao = descricao;
    }

    public long getIdItemServico() { return idItemServico; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Date getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(Date d) { this.dataRealizacao = d; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Funcionario getResponsavel() { return responsavel; }
    public void setResponsavel(Funcionario r) { this.responsavel = r; }
    public List<Peca> getListaPecas() { return listaPecas; }
}
