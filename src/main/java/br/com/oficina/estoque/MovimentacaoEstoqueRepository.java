package br.com.oficina.estoque;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoEstoqueRepository {
    private final Tabela tMov;

    public MovimentacaoEstoqueRepository(Conexao con) {
        this.tMov = con.tabela("movimentacao_estoque");
    }

    private MovimentacaoEstoqueEntity map(Registro r) {
        String idServ = r.get("id_servico");
        return new MovimentacaoEstoqueEntity(
            r.getLong("id_movimentacao"),
            r.getLong("id_peca"),
            r.get("tipo"),
            r.getInt("quantidade"),
            r.get("data_movimentacao"),
            r.get("origem"),
            (idServ == null || idServ.isEmpty()) ? null : Long.parseLong(idServ),
            r.get("observacao"));
    }

    public MovimentacaoEstoqueEntity salvar(MovimentacaoEstoqueEntity m) {
        long id = tMov.inserir(new Registro()
            .set("id_peca", m.getIdPeca())
            .set("tipo", m.getTipo())
            .set("quantidade", m.getQuantidade())
            .set("data_movimentacao", m.getDataMovimentacao())
            .set("origem", m.getOrigem())
            .set("id_servico", m.getIdServico())
            .set("observacao", m.getObservacao()));
        m.setIdMovimentacao(id);
        return m;
    }

    public List<MovimentacaoEstoqueEntity> listarTodas() {
        List<MovimentacaoEstoqueEntity> out = new ArrayList<>();
        for (Registro r : tMov.registros()) out.add(map(r));
        return out;
    }
}
