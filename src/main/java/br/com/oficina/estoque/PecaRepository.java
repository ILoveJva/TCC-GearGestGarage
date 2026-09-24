package br.com.oficina.estoque;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class PecaRepository {
    private final Tabela tPeca;

    public PecaRepository(Conexao con) {
        this.tPeca = con.tabela("peca");
    }

    private PecaEntity map(Registro r) {
        PecaEntity p = new PecaEntity(r.getLong("id_peca"), r.getLong("id_catalogo_peca"),
            r.get("nome_tecnico"), r.get("fabricante"), r.getDouble("valor"));
        p.setQuantidadeEstoque(r.getInt("quantidade_estoque"));
        return p;
    }

    public PecaEntity salvar(long idCatalogoPeca, String nomeTecnico, String fabricante, double valor) {
        long id = tPeca.inserir(new Registro()
            .set("id_catalogo_peca", idCatalogoPeca)
            .set("nome_tecnico", nomeTecnico != null ? nomeTecnico : "")
            .set("fabricante", fabricante != null ? fabricante : "")
            .set("valor", valor)
            .set("quantidade_estoque", 0));
        PecaEntity p = new PecaEntity(id, idCatalogoPeca, nomeTecnico, fabricante, valor);
        p.setQuantidadeEstoque(0);
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

    public List<PecaEntity> listarPorCatalogo(long idCatalogoPeca) {
        List<PecaEntity> out = new ArrayList<>();
        for (Registro r : tPeca.filtrar(x -> x.getLong("id_catalogo_peca") == idCatalogoPeca)) out.add(map(r));
        return out;
    }

    /** Procura uma peça real já cadastrada para o catálogo com o mesmo nome técnico + fabricante; senão cadastra uma nova. */
    public PecaEntity buscarOuCriar(long idCatalogoPeca, String nomeTecnico, String fabricante, double valor) {
        String nt = nomeTecnico != null ? nomeTecnico.trim() : "";
        String fab = fabricante != null ? fabricante.trim() : "";
        for (PecaEntity p : listarPorCatalogo(idCatalogoPeca)) {
            if (p.getNomeTecnico().trim().equalsIgnoreCase(nt) && p.getFabricante().trim().equalsIgnoreCase(fab)) {
                return p;
            }
        }
        return salvar(idCatalogoPeca, nomeTecnico, fabricante, valor);
    }

    /** Ajusta o estoque da peça real em delta (positivo entra, negativo sai). Retorna a nova quantidade. */
    public int ajustarEstoque(long idPeca, int delta) {
        Registro r = tPeca.buscarPorId(idPeca);
        if (r == null) return 0;
        int nova = r.getInt("quantidade_estoque") + delta;
        tPeca.atualizar(idPeca, "quantidade_estoque", nova);
        return nova;
    }
}
