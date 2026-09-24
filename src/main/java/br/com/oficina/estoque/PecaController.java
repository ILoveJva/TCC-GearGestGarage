package br.com.oficina.estoque;

import java.util.List;

public class PecaController {
    private final PecaService service;
    public PecaController(PecaService service) { this.service = service; }

    public PecaEntity entidade(long id) { return service.buscarEntidade(id); }
    public List<PecaEntity> listarTodas() { return service.listarTodas(); }
    public List<PecaEntity> listarPorCatalogo(long idCatalogoPeca) { return service.listarPorCatalogo(idCatalogoPeca); }
    public PecaEntity buscarOuCriar(long idCatalogoPeca, String nomeTecnico, String fabricante, double valor) {
        return service.buscarOuCriar(idCatalogoPeca, nomeTecnico, fabricante, valor);
    }
    public int quantidadeEstoqueCatalogo(long idCatalogoPeca) { return service.quantidadeEstoqueCatalogo(idCatalogoPeca); }
}
