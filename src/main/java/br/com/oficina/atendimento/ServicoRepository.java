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

    private Long longOuNull(Registro r, String coluna) {
        String v = r.get(coluna);
        if (v == null || v.isBlank()) return null;
        try { return Long.parseLong(v.trim()); } catch (NumberFormatException e) { return null; }
    }

    private ItemServicoEntity mapItem(Registro r) {
        return new ItemServicoEntity(
            r.getLong("id_item_servico"), r.getInt("etapa"), r.get("codigo"),
            r.get("descricao"), r.get("status"), r.get("tempo_gasto"), r.get("data_realizacao"),
            longOuNull(r, "id_peca"), r.getLong("id_servico"),
            longOuNull(r, "id_funcionario"));
    }

    private ServicoEntity mapServico(Registro r) {
        ServicoEntity s = new ServicoEntity(
            r.getLong("id_servico"), r.get("codigo"),
            r.get("data_servico"), r.getInt("km_servico"), r.get("titulo"),
            r.get("tipo_servico"), r.get("status"),
            r.getLong("id_veiculo"), r.getLong("id_oficina"),
            longOuNull(r, "id_orcamento"));
        s.setTipoManutencao(r.get("tipo_manutencao"));
        s.setItens(itensDe(s.getIdServico()));
        return s;
    }

    public ServicoEntity salvar(ServicoEntity s) {
        // id_orcamento nullable: 0 ou null vira NULL no banco
        Long idOrc = (s.getIdOrcamento() == null || s.getIdOrcamento() == 0) ? null : s.getIdOrcamento();
        long id = tServico.inserir(new Registro()
            .set("codigo", "")
            .set("data_servico", s.getDataServico())
            .set("km_servico", s.getKmServico())
            .set("titulo", s.getTitulo())
            .set("tipo_servico", s.getTipoServico())
            .set("tipo_manutencao", s.getTipoManutencao())
            .set("status", s.getStatus())
            .set("id_veiculo", s.getIdVeiculo())
            .set("id_oficina", s.getIdOficina())
            .set("id_orcamento", idOrc));
        s.setIdServico(id);

        long orcRef = idOrc != null ? idOrc : 0L;
        String codigo = String.format("%04d.%03d.%04d", orcRef, s.getIdOficina(), id);
        tServico.atualizar(id, "codigo", codigo);
        s.setCodigo(codigo);
        return s;
    }

    public ItemServicoEntity salvarItem(ItemServicoEntity i) {
        // id_peca e id_funcionario sao nullable
        Long idPeca = (i.getIdPeca() == null || i.getIdPeca() == 0) ? null : i.getIdPeca();
        Long idFunc = (i.getIdFuncionario() == null || i.getIdFuncionario() == 0) ? null : i.getIdFuncionario();
        long id = tItem.inserir(new Registro()
            .set("etapa", i.getEtapa())
            .set("codigo", "")
            .set("descricao", i.getDescricao())
            .set("status", i.getStatus())
            .set("tempo_gasto", i.getTempoGasto())
            .set("data_realizacao", i.getDataRealizacao())
            .set("id_peca", idPeca)
            .set("id_servico", i.getIdServico())
            .set("id_funcionario", idFunc));
        i.setIdItemServico(id);

        String codigo = String.format("%04d.%02d.%04d", i.getIdServico(), i.getEtapa(), id);
        tItem.atualizar(id, "codigo", codigo);
        i.setCodigo(codigo);
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

    /** Servico aberto a partir do orcamento informado, ou null se nao houver. */
    public ServicoEntity buscarPorOrcamento(long idOrcamento) {
        for (Registro r : tServico.filtrar(x -> x.getLong("id_orcamento") == idOrcamento))
            return mapServico(r);
        return null;
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
