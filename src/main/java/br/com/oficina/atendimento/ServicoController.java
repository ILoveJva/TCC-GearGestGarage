package br.com.oficina.atendimento;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public class ServicoController {
    private final ServicoService service;
    public ServicoController(ServicoService service) { this.service = service; }

    public ServicoResponseDTO abrir(String titulo, String tipoServico, String tipoManutencao,
                                    String dataServico, int km,
                                    long idVeiculo, long idOficina, long idOrcamento) {
        return service.paraDTO(service.abrir(titulo, tipoServico, tipoManutencao,
            dataServico, km, idVeiculo, idOficina, idOrcamento));
    }
    public ServicoResponseDTO finalizar(long idServico, String observacaoSaida, Long idFuncionario) {
        return service.paraDTO(service.finalizar(idServico, observacaoSaida, idFuncionario, LocalDate.now().toString()));
    }
    public ServicoResponseDTO registrarEtapaOS(long idServico, String descricao,
                                               java.util.List<String[]> checklist) {
        service.registrarEtapaOS(idServico, descricao, checklist);
        return service.paraDTO(service.buscar(idServico));
    }
    public List<ServicoResponseDTO> todas() { return service.listar(); }
    public ServicoResponseDTO porId(long id) { return service.paraDTO(service.buscar(id)); }
    public void congelarPorOrcamento(long idOrcamento) { service.congelarPorOrcamento(idOrcamento); }
    public void reabrirPorOrcamento(long idOrcamento) { service.reabrirPorOrcamento(idOrcamento); }
}
