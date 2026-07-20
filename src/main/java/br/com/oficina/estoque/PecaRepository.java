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
        return new PecaEntity(r.getLong("id_peca"), r.get("nome_popular"),
            r.get("vida_util_tempo"), r.get("vida_util_km"), r.get("sistema"));
    }

    public PecaEntity salvar(PecaEntity p) {
        long id = tPeca.inserir(new Registro()
            .set("nome_popular", p.getNomePopular())
            .set("vida_util_tempo", p.getVidaUtilTempo())
            .set("vida_util_km", p.getVidaUtilKm())
            .set("sistema", p.getSistema()));
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

    public FabricantePecaEntity salvarFabricante(FabricantePecaEntity f) {
        long id = tFabricante.inserir(new Registro().set("nome", f.getNome()).set("pais", f.getPais()));
        f.setIdFabricantePeca(id);
        return f;
    }
    public void atualizar(PecaEntity p) {
        tPeca.atualizar(p.getIdPeca(), "nome_popular",    p.getNomePopular());
        tPeca.atualizar(p.getIdPeca(), "vida_util_tempo", p.getVidaUtilTempo());
        tPeca.atualizar(p.getIdPeca(), "vida_util_km",    p.getVidaUtilKm());
        tPeca.atualizar(p.getIdPeca(), "sistema",         p.getSistema());
    }

    public List<FabricantePecaEntity> listarFabricantes() {
        List<FabricantePecaEntity> out = new ArrayList<>();
        for (Registro r : tFabricante.registros())
            out.add(new FabricantePecaEntity(r.getLong("id_fabricante_peca"), r.get("nome"), r.get("pais")));
        return out;
    }
}
