package br.com.oficina.estoque;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoPecaRepository {
    private final Tabela tCatalogoPeca;

    public CatalogoPecaRepository(Conexao con) {
        this.tCatalogoPeca = con.tabela("catalogo_peca");
    }

    private CatalogoPecaEntity map(Registro r) {
        return new CatalogoPecaEntity(r.getLong("id_catalogo_peca"), r.get("nome_popular"),
            r.get("vida_util_tempo"), r.get("vida_util_km"), r.get("sistema"));
    }

    public CatalogoPecaEntity salvar(CatalogoPecaEntity p) {
        long id = tCatalogoPeca.inserir(new Registro()
            .set("nome_popular", p.getNomePopular())
            .set("vida_util_tempo", p.getVidaUtilTempo())
            .set("vida_util_km", p.getVidaUtilKm())
            .set("sistema", p.getSistema()));
        p.setIdCatalogoPeca(id);
        return p;
    }
    public CatalogoPecaEntity buscarPorId(long id) {
        Registro r = tCatalogoPeca.buscarPorId(id);
        return r == null ? null : map(r);
    }
    public List<CatalogoPecaEntity> listarTodas() {
        List<CatalogoPecaEntity> out = new ArrayList<>();
        for (Registro r : tCatalogoPeca.registros()) out.add(map(r));
        return out;
    }
    public void atualizar(CatalogoPecaEntity p) {
        tCatalogoPeca.atualizar(p.getIdCatalogoPeca(), "nome_popular",    p.getNomePopular());
        tCatalogoPeca.atualizar(p.getIdCatalogoPeca(), "vida_util_tempo", p.getVidaUtilTempo());
        tCatalogoPeca.atualizar(p.getIdCatalogoPeca(), "vida_util_km",    p.getVidaUtilKm());
        tCatalogoPeca.atualizar(p.getIdCatalogoPeca(), "sistema",         p.getSistema());
    }
}
