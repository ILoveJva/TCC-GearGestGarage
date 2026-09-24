package br.com.oficina.estoque;

import br.com.oficina.estoque.dto.PecaResponseDTO;
import java.util.ArrayList;
import java.util.List;

public class CatalogoPecaService {
    private final CatalogoPecaRepository repository;
    public CatalogoPecaService(CatalogoPecaRepository repository) { this.repository = repository; }

    public CatalogoPecaEntity cadastrar(String nomePopular, String vidaUtilTempo, String vidaUtilKm, String sistema) {
        return repository.salvar(new CatalogoPecaEntity(null, nomePopular, vidaUtilTempo, vidaUtilKm, sistema));
    }
    public List<CatalogoPecaEntity> listarTodasEntidades() {
        return repository.listarTodas();
    }

    public void atualizar(long id, String nome, String vidaTempo, String vidaKm, String sistema) {
        CatalogoPecaEntity p = repository.buscarPorId(id);
        if (p == null) throw new br.com.oficina.shared.exception.RecursoNaoEncontradoException("Peca " + id + " nao encontrada");
        p.setNomePopular(nome);
        p.setVidaUtilTempo(vidaTempo);
        p.setVidaUtilKm(vidaKm);
        p.setSistema(sistema);
        repository.atualizar(p);
    }

    public List<PecaResponseDTO> listar() {
        List<PecaResponseDTO> out = new ArrayList<>();
        for (CatalogoPecaEntity p : repository.listarTodas()) out.add(paraDTO(p));
        return out;
    }
    public PecaResponseDTO buscarDTO(long id) {
        CatalogoPecaEntity p = repository.buscarPorId(id);
        if (p == null) throw new br.com.oficina.shared.exception.RecursoNaoEncontradoException("Peca " + id + " nao encontrada");
        return paraDTO(p);
    }
    public CatalogoPecaEntity buscarEntidade(long id) {
        return repository.buscarPorId(id);
    }
    private PecaResponseDTO paraDTO(CatalogoPecaEntity p) {
        return new PecaResponseDTO(p.getIdCatalogoPeca(), p.getNomeExibicao(), p.getVidaUtilKm(), p.getVidaUtilTempo());
    }
}
