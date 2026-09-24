package br.com.oficina.estoque;

import br.com.oficina.estoque.dto.PecaResponseDTO;
import java.util.List;

public class CatalogoPecaController {
    private final CatalogoPecaService service;
    public CatalogoPecaController(CatalogoPecaService service) { this.service = service; }

    public CatalogoPecaEntity cadastrar(String nomePopular, String vidaUtilTempo, String vidaUtilKm, String sistema) {
        return service.cadastrar(nomePopular, vidaUtilTempo, vidaUtilKm, sistema);
    }
    public List<CatalogoPecaEntity> listarTodasEntidades() {
        return service.listarTodasEntidades();
    }

    public void atualizar(long id, String nome, String vidaTempo, String vidaKm, String sistema) {
        service.atualizar(id, nome, vidaTempo, vidaKm, sistema);
    }

    public PecaResponseDTO porId(long id) { return service.buscarDTO(id); }
    public CatalogoPecaEntity entidade(long id) { return service.buscarEntidade(id); }
    public List<PecaResponseDTO> todas() { return service.listar(); }
}
