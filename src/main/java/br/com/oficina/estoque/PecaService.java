package br.com.oficina.estoque;

import java.util.List;

public class PecaService {
    private final PecaRepository repository;
    public PecaService(PecaRepository repository) { this.repository = repository; }

    public PecaEntity buscarEntidade(long id) { return repository.buscarPorId(id); }
    public List<PecaEntity> listarTodas() { return repository.listarTodas(); }
    public List<PecaEntity> listarPorCatalogo(long idCatalogoPeca) { return repository.listarPorCatalogo(idCatalogoPeca); }

    public PecaEntity buscarOuCriar(long idCatalogoPeca, String nomeTecnico, String fabricante, double valor) {
        return repository.buscarOuCriar(idCatalogoPeca, nomeTecnico, fabricante, valor);
    }

    public int quantidadeEstoqueCatalogo(long idCatalogoPeca) {
        int total = 0;
        for (PecaEntity p : repository.listarPorCatalogo(idCatalogoPeca)) total += p.getQuantidadeEstoque();
        return total;
    }
}
