package br.com.oficina.atendimento;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoRepository {
    private final Tabela tServico;
    private final Tabela tItem;

    public ServicoRepository(Conexao con) {
        this.tServico = con.tabela("servico");
        this.tItem = con.tabela("item_servico");
    }

    private ItemServicoEntity mapItem(Registro r) {
        return new ItemServicoEntity(r.getLong("id_item_servico"), r.get("descricao"),
            r.get("status"), r.get("data_realizacao"), r.getLong("id_peca"),
            r.getLong("id_servico"), r.getLong("id_funcionario"));
    }
    private ServicoEntity mapServico(Registro r) {
        ServicoEntity s = new ServicoEntity(r.getLong("id_servico"), r.get("data_servico"),
            r.getInt("km_servico"), r.get("titulo"), r.get("tipo_servico"), r.get("status"),
            r.getLong("id_veiculo"), r.getLong("id_oficina"), r.getLong("id_orcamento"));
        s.setItens(itensDe(s.getIdServico()));
        return s;
    }

    public ServicoEntity salvar(ServicoEntity s) {
        long id = tServico.inserir(new Registro()
            .set("data_servico", s.getDataServico()).set("km_servico", s.getKmServico())
            .set("titulo", s.getTitulo()).set("tipo_servico", s.getTipoServico())
            .set("status", s.getStatus()).set("id_veiculo", s.getIdVeiculo())
            .set("id_oficina", s.getIdOficina()).set("id_orcamento", s.getIdOrcamento()));
        s.setIdServico(id);
        return s;
    }
    public ItemServicoEntity salvarItem(ItemServicoEntity i) {
        long id = tItem.inserir(new Registro()
            .set("descricao", i.getDescricao()).set("status", i.getStatus())
            .set("data_realizacao", i.getDataRealizacao()).set("id_peca", i.getIdPeca())
            .set("id_servico", i.getIdServico()).set("id_funcionario", i.getIdFuncionario()));
        i.setIdItemServico(id);
        return i;
    }
    public ServicoEntity buscarPorId(long id) {
        Registro r = tServico.buscarPorId(id);
        return r == null ? null : mapServico(r);
    }
    public List<ItemServicoEntity> itensDe(long idServico) {
        List<ItemServicoEntity> out = new ArrayList<>();
        for (Registro r : tItem.filtrar(i -> i.getLong("id_servico") == idServico)) out.add(mapItem(r));
        return out;
    }
    public List<ServicoEntity> listarTodos() {
        List<ServicoEntity> out = new ArrayList<>();
        for (Registro r : tServico.registros()) out.add(mapServico(r));
        return out;
    }
    public void atualizarStatus(long idServico, String status) {
        tServico.atualizar(idServico, "status", status);
    }
    public void concluirItens(long idServico, String data) {
        for (ItemServicoEntity it : itensDe(idServico)) {
            tItem.atualizar(it.getIdItemServico(), "status", "CONCLUIDO");
            tItem.atualizar(it.getIdItemServico(), "data_realizacao", data);
        }
    }
}
