package br.com.oficina.atendimento;

import br.com.oficina.atendimento.dto.*;
import java.util.List;

public class OrcamentoController {
    private final OrcamentoService service;
    public OrcamentoController(OrcamentoService service) { this.service = service; }

    public OrcamentoResponseDTO criar(double valor, long idPeca, long idVeiculo, long idCliente, long idFuncionario) {
        return service.paraDTO(service.criar(new OrcamentoRequestDTO(valor, idPeca, idVeiculo, idCliente, idFuncionario)));
    }
    public List<OrcamentoResponseDTO> todos() { return service.listar(); }
    public OrcamentoResponseDTO porId(long id) { return service.paraDTO(service.buscar(id)); }
}
