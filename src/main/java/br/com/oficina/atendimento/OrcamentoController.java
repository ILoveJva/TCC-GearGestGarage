package br.com.oficina.atendimento;

import br.com.oficina.atendimento.dto.*;
import java.util.List;

public class OrcamentoController {
    private final OrcamentoService service;
    public OrcamentoController(OrcamentoService service) { this.service = service; }

    public OrcamentoResponseDTO criar(double valor, String responsavel, String reclamacao,
                                      String dataCriacao, Long idPeca, long idVeiculo,
                                      long idCliente, Long idFuncionario) {
        return service.paraDTO(service.criar(new OrcamentoRequestDTO(valor, "ENTRADA",
            responsavel, reclamacao, dataCriacao, idPeca, idVeiculo, idCliente, idFuncionario, null)));
    }

    public OrcamentoResponseDTO criarRevisao(long idServico, double valor, String responsavel,
                                             String reclamacao, long idVeiculo, long idCliente,
                                             Long idFuncionario) {
        return service.paraDTO(service.criarRevisao(idServico, valor, responsavel, reclamacao,
            idVeiculo, idCliente, idFuncionario));
    }

    public void aprovar(long id) { service.aprovar(id); }
    public void reprovar(long id) { service.reprovar(id); }
    public void atualizarValor(long id, double valor) { service.atualizarValor(id, valor); }
    public List<OrcamentoResponseDTO> todos() { return service.listar(); }
    public List<OrcamentoResponseDTO> entrada() { return service.listarEntrada(); }
    public OrcamentoResponseDTO porId(long id) { return service.paraDTO(service.buscar(id)); }
}
