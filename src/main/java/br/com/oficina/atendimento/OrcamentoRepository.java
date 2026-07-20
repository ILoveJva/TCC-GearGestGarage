package br.com.oficina.atendimento;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class OrcamentoRepository {
    private final Tabela t;
    public OrcamentoRepository(Conexao con) { this.t = con.tabela("orcamento"); }

    private Long longOuNull(Registro r, String coluna) {
        String v = r.get(coluna);
        if (v == null || v.isBlank()) return null;
        try { return Long.parseLong(v.trim()); } catch (NumberFormatException e) { return null; }
    }

    private OrcamentoEntity map(Registro r) {
        return new OrcamentoEntity(
            r.getLong("id_orcamento"), r.getDouble("valor"),
            r.get("codigo"), r.get("tipo"),
            r.get("responsavel"), r.get("reclamacao"), r.get("data_criacao"), r.get("status"),
            longOuNull(r, "id_peca"), longOuNull(r, "id_veiculo"),
            longOuNull(r, "id_cliente"), longOuNull(r, "id_funcionario"),
            longOuNull(r, "id_servico_revisao"));
    }

    public OrcamentoEntity salvar(OrcamentoEntity o) {
        long id = t.inserir(new Registro()
            .set("valor", o.getValor())
            .set("codigo", "")
            .set("tipo", o.getTipo() != null ? o.getTipo() : "ENTRADA")
            .set("responsavel", o.getResponsavel())
            .set("reclamacao", o.getReclamacao())
            .set("data_criacao", o.getDataCriacao())
            .set("status", o.getStatus() != null ? o.getStatus() : "PENDENTE")
            .set("id_peca", o.getIdPeca())
            .set("id_veiculo", o.getIdVeiculo())
            .set("id_cliente", o.getIdCliente())
            .set("id_funcionario", o.getIdFuncionario())
            .set("id_servico_revisao", o.getIdServicoRevisao()));
        o.setIdOrcamento(id);

        long idVeic = o.getIdVeiculo() != null ? o.getIdVeiculo() : 0L;
        String codigo = String.format("%04d.%04d", idVeic, id);
        t.atualizar(id, "codigo", codigo);
        o.setCodigo(codigo);
        return o;
    }

    public boolean atualizarStatus(long id, String status) {
        return t.atualizar(id, "status", status);
    }

    public boolean atualizarValor(long id, double valor) {
        return t.atualizar(id, "valor", valor);
    }

    public boolean atualizarServicoRevisao(long idOrcamento, long idServico) {
        return t.atualizar(idOrcamento, "id_servico_revisao", idServico);
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

    public List<OrcamentoEntity> listarPorTipo(String tipo) {
        List<OrcamentoEntity> out = new ArrayList<>();
        for (Registro r : t.filtrar(re -> tipo.equalsIgnoreCase(re.get("tipo")))) out.add(map(r));
        return out;
    }
}
