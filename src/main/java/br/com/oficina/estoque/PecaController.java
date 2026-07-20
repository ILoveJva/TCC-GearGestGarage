package br.com.oficina.estoque;

import br.com.oficina.estoque.dto.PecaResponseDTO;
import java.util.List;

public class PecaController {
    private final PecaService service;
    public PecaController(PecaService service) { this.service = service; }

    public PecaEntity cadastrar(String nomePopular, String vidaUtilTempo, String vidaUtilKm, String sistema) {
        return service.cadastrar(nomePopular, vidaUtilTempo, vidaUtilKm, sistema);
    }
    public List<PecaEntity> listarTodasEntidades() {
        return service.listarTodasEntidades();
    }

    public void atualizar(long id, String nome, String vidaTempo, String vidaKm, String sistema) {
        service.atualizar(id, nome, vidaTempo, vidaKm, sistema);
    }

    public PecaResponseDTO porId(long id) { return service.buscarDTO(id); }
    public PecaEntity entidade(long id) { return service.buscarEntidade(id); }
    public List<PecaResponseDTO> todas() { return service.listar(); }
}
