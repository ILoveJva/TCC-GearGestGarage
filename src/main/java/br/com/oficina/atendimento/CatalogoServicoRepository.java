package br.com.oficina.atendimento;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class CatalogoServicoRepository {
    private final Tabela tCatalogo;
    private final Tabela tOrcamentoServico;
    private final Tabela tCatalogoPeca;

    public CatalogoServicoRepository(Conexao con) {
        this.tCatalogo        = con.tabela("catalogo_servico");
        this.tOrcamentoServico = con.tabela("orcamento_servico");
        this.tCatalogoPeca    = con.tabela("catalogo_servico_peca");
    }

    private Integer intOuNull(Registro r, String col) {
        String v = r.get(col);
        if (v == null || v.isBlank()) return null;
        try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { return null; }
    }

    private CatalogoServicoEntity map(Registro r) {
        return new CatalogoServicoEntity(
            r.getLong("id_catalogo_servico"),
            r.get("nome"), r.get("descricao"), r.getDouble("valor"),
            r.get("tipo"), r.get("sistema"),
            intOuNull(r, "validade_km"), intOuNull(r, "validade_meses"));
    }

    public CatalogoServicoEntity salvar(CatalogoServicoEntity e) {
        Registro reg = new Registro()
            .set("nome", e.getNome())
            .set("descricao", e.getDescricao())
            .set("valor", e.getValor())
            .set("tipo", e.getTipo())
            .set("sistema", e.getSistema());
        if (e.getValidadeKm() != null)    reg.set("validade_km", e.getValidadeKm());
        if (e.getValidadeMeses() != null) reg.set("validade_meses", e.getValidadeMeses());
        long id = tCatalogo.inserir(reg);
        e.setIdCatalogoServico(id);
        return e;
    }

    public List<CatalogoServicoEntity> listarTodos() {
        List<CatalogoServicoEntity> out = new ArrayList<>();
        for (Registro r : tCatalogo.registros()) out.add(map(r));
        return out;
    }

    public List<CatalogoServicoEntity> listarPorOrcamento(long idOrcamento) {
        List<CatalogoServicoEntity> out = new ArrayList<>();
        for (Registro r : tOrcamentoServico.filtrar(x -> x.getLong("id_orcamento") == idOrcamento)) {
            long idCat = r.getLong("id_catalogo_servico");
            Registro cat = tCatalogo.buscarPorId(idCat);
            if (cat != null) out.add(map(cat));
        }
        return out;
    }

    public void vincularAOrcamento(long idOrcamento, long idCatalogoServico, double valorCobrado) {
        tOrcamentoServico.inserir(new Registro()
            .set("id_orcamento", idOrcamento)
            .set("id_catalogo_servico", idCatalogoServico)
            .set("valor_cobrado", valorCobrado));
    }

    public java.util.Map<Long, Double> listarValoresItensPorOrcamento(long idOrcamento) {
        java.util.Map<Long, Double> out = new java.util.HashMap<>();
        for (Registro r : tOrcamentoServico.filtrar(x -> x.getLong("id_orcamento") == idOrcamento))
            out.put(r.getLong("id_catalogo_servico"), r.getDouble("valor_cobrado"));
        return out;
    }

    public void removerItensDoOrcamento(long idOrcamento) {
        for (Registro r : tOrcamentoServico.filtrar(x -> x.getLong("id_orcamento") == idOrcamento))
            tOrcamentoServico.remover(r.getLong("id_orcamento_servico"));
    }

    public CatalogoServicoEntity buscarPorId(long id) {
        Registro r = tCatalogo.buscarPorId(id);
        return r == null ? null : map(r);
    }

    public void atualizar(CatalogoServicoEntity e) {
        long id = e.getIdCatalogoServico();
        tCatalogo.atualizar(id, "nome",      e.getNome());
        tCatalogo.atualizar(id, "descricao", e.getDescricao());
        tCatalogo.atualizar(id, "valor",     e.getValor());
        tCatalogo.atualizar(id, "tipo",      e.getTipo());
        tCatalogo.atualizar(id, "sistema",   e.getSistema());
        tCatalogo.atualizar(id, "validade_km",
            e.getValidadeKm() != null ? e.getValidadeKm().toString() : null);
        tCatalogo.atualizar(id, "validade_meses",
            e.getValidadeMeses() != null ? e.getValidadeMeses().toString() : null);
    }

    /** IDs dos orçamentos que contêm este item de catálogo. */
    public List<Long> listarIdOrcamentosComItem(long idCatalogoServico) {
        List<Long> out = new ArrayList<>();
        for (Registro r : tOrcamentoServico.filtrar(x -> x.getLong("id_catalogo_servico") == idCatalogoServico))
            out.add(r.getLong("id_orcamento"));
        return out;
    }

    // ---- Peças padrão do catálogo ----
    public void vincularPecaAoCatalogo(long idCatalogoServico, long idPeca) {
        tCatalogoPeca.inserir(new Registro()
            .set("id_catalogo_servico", idCatalogoServico)
            .set("id_peca", idPeca));
    }

    public List<Long> listarIdsPecasDoCatalogo(long idCatalogoServico) {
        List<Long> out = new ArrayList<>();
        for (Registro r : tCatalogoPeca.filtrar(x -> x.getLong("id_catalogo_servico") == idCatalogoServico))
            out.add(r.getLong("id_peca"));
        return out;
    }

    /** Retorna os pares {id_catalogo_servico_peca, id_peca} para uso na remoção individual. */
    public java.util.Map<Long, Long> listarLinksPecasDoCatalogo(long idCatalogoServico) {
        java.util.Map<Long, Long> out = new java.util.LinkedHashMap<>();
        for (Registro r : tCatalogoPeca.filtrar(x -> x.getLong("id_catalogo_servico") == idCatalogoServico))
            out.put(r.getLong("id_catalogo_servico_peca"), r.getLong("id_peca"));
        return out;
    }

    public void removerLinkPeca(long idCatalogoServicoPeca) {
        tCatalogoPeca.remover(idCatalogoServicoPeca);
    }

    public void removerPecasDoCatalogo(long idCatalogoServico) {
        for (Registro r : tCatalogoPeca.filtrar(x -> x.getLong("id_catalogo_servico") == idCatalogoServico))
            tCatalogoPeca.remover(r.getLong("id_catalogo_servico_peca"));
    }
}
