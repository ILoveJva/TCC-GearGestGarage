package br.com.oficina.estoque;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class PecaRepository {
    private final Tabela tPeca;
    private final Tabela tFabricante;

    public PecaRepository(Conexao con) {
        this.tPeca = con.tabela("peca");
        this.tFabricante = con.tabela("fabricante_peca");
    }

    private PecaEntity map(Registro r) {
        return new PecaEntity(r.getLong("id_peca"), r.get("nome_peca"),
            r.get("vida_util_tempo"), r.get("vida_util_km"),
            r.getLong("id_fabricante_peca"), r.getLong("id_veiculo"));
    }

    public PecaEntity salvar(PecaEntity p) {
        long id = tPeca.inserir(new Registro()
            .set("nome_peca", p.getNomePeca()).set("vida_util_tempo", p.getVidaUtilTempo())
            .set("vida_util_km", p.getVidaUtilKm()).set("id_fabricante_peca", p.getIdFabricantePeca())
            .set("id_veiculo", p.getIdVeiculo()));
        p.setIdPeca(id);
        return p;
    }
    public PecaEntity buscarPorId(long id) {
        Registro r = tPeca.buscarPorId(id);
        return r == null ? null : map(r);
    }
    public List<PecaEntity> listarTodas() {
        List<PecaEntity> out = new ArrayList<>();
        for (Registro r : tPeca.registros()) out.add(map(r));
        return out;
    }
    public List<PecaEntity> listarPorVeiculo(long idVeiculo) {
        List<PecaEntity> out = new ArrayList<>();
        for (Registro r : tPeca.filtrar(p -> p.getLong("id_veiculo") == idVeiculo)) out.add(map(r));
        return out;
    }

    public FabricantePecaEntity salvarFabricante(FabricantePecaEntity f) {
        long id = tFabricante.inserir(new Registro().set("nome", f.getNome()).set("pais", f.getPais()));
        f.setIdFabricantePeca(id);
        return f;
    }
    public FabricantePecaEntity buscarFabricante(long id) {
        Registro r = tFabricante.buscarPorId(id);
        return r == null ? null : new FabricantePecaEntity(r.getLong("id_fabricante_peca"), r.get("nome"), r.get("pais"));
    }
}
