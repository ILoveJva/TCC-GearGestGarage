package br.com.oficina.atendimento;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class OrcamentoRepository {
    private final Tabela t;
    public OrcamentoRepository(Conexao con) { this.t = con.tabela("orcamento"); }

    private OrcamentoEntity map(Registro r) {
        return new OrcamentoEntity(r.getLong("id_orcamento"), r.getDouble("valor"),
            r.getLong("id_peca"), r.getLong("id_veiculo"), r.getLong("id_cliente"), r.getLong("id_funcionario"));
    }
    public OrcamentoEntity salvar(OrcamentoEntity o) {
        long id = t.inserir(new Registro()
            .set("valor", o.getValor()).set("id_peca", o.getIdPeca()).set("id_veiculo", o.getIdVeiculo())
            .set("id_cliente", o.getIdCliente()).set("id_funcionario", o.getIdFuncionario()));
        o.setIdOrcamento(id);
        return o;
    }
    public OrcamentoEntity buscarPorId(long id) {
        Registro r = t.buscarPorId(id);
        return r == null ? null : map(r);
    }
    public List<OrcamentoEntity> listarTodos() {
        List<OrcamentoEntity> out = new ArrayList<>();
        for (Registro r : t.registros()) out.add(map(r));
        return out;
    }
}
