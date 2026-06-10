package br.com.oficina.atendimento;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import java.util.List;

public class ServicoController {
    private final ServicoService service;
    public ServicoController(ServicoService service) { this.service = service; }

    public ServicoResponseDTO abrir(String titulo, String tipoServico, String dataServico, int km,
                                    long idVeiculo, long idOficina, long idOrcamento) {
        return service.paraDTO(service.abrir(titulo, tipoServico, dataServico, km, idVeiculo, idOficina, idOrcamento));
    }
    public ServicoResponseDTO realizar(long idServico) {
        return service.paraDTO(service.realizar(idServico, "2026-06-09"));
    }
    public List<ServicoResponseDTO> todas() { return service.listar(); }
    public ServicoResponseDTO porId(long id) { return service.paraDTO(service.buscar(id)); }
}
