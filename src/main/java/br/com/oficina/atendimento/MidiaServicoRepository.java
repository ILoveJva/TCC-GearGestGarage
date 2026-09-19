package br.com.oficina.atendimento;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class MidiaServicoRepository {
    private final Tabela tMidia;

    public MidiaServicoRepository(Conexao con) {
        this.tMidia = con.tabela("midias_ordemservico");
    }

    private MidiaServicoEntity mapear(Registro r) {
        return new MidiaServicoEntity(
            r.getLong("id_midia"), r.getLong("id_servico"), r.get("tipo"),
            r.get("nome_arquivo"), r.get("caminho"), r.get("descricao"), r.get("data_upload"));
    }

    public MidiaServicoEntity salvar(MidiaServicoEntity m) {
        long id = tMidia.inserir(new Registro()
            .set("id_servico", m.getIdServico())
            .set("tipo", m.getTipo())
            .set("nome_arquivo", m.getNomeArquivo())
            .set("caminho", m.getCaminho())
            .set("descricao", m.getDescricao())
            .set("data_upload", m.getDataUpload()));
        m.setIdMidia(id);
        return m;
    }

    public MidiaServicoEntity buscarPorId(long id) {
        Registro r = tMidia.buscarPorId(id);
        return r == null ? null : mapear(r);
    }

    public List<MidiaServicoEntity> listarPorServico(long idServico) {
        List<MidiaServicoEntity> out = new ArrayList<>();
        for (Registro r : tMidia.filtrar(x -> x.getLong("id_servico") == idServico)) out.add(mapear(r));
        return out;
    }

    public boolean remover(long idMidia) {
        return tMidia.remover(idMidia);
    }
}
