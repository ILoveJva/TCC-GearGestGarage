package br.com.oficina.estoque;

import br.com.oficina.estoque.dto.PecaResponseDTO;
import java.util.List;

public class PecaController {
    private final PecaService service;
    public PecaController(PecaService service) { this.service = service; }

    public FabricantePecaEntity cadastrarFabricante(String nome, String pais) {
        return service.cadastrarFabricante(nome, pais);
    }
    public PecaEntity cadastrar(String nomePeca, String vidaUtilTempo, String vidaUtilKm,
                                long idFabricante, long idVeiculo) {
        return service.cadastrar(nomePeca, vidaUtilTempo, vidaUtilKm, idFabricante, idVeiculo);
    }
    public PecaResponseDTO porId(long id) { return service.buscarDTO(id); }
    public List<PecaResponseDTO> todas() { return service.listar(); }
}
