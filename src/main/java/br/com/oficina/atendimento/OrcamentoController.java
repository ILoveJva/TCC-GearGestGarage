package br.com.oficina.atendimento;

import br.com.oficina.atendimento.dto.*;
import java.util.List;

public class OrcamentoController {
    private final OrcamentoService service;
    public OrcamentoController(OrcamentoService service) { this.service = service; }

    public OrcamentoResponseDTO criar(double valor, String responsavel, String reclamacao,
                                      String dataCriacao, Long idPeca, long idVeiculo,
                                      long idCliente, Long idFuncionario, long idOficina) {
        return service.paraDTO(service.criar(new OrcamentoRequestDTO(valor, "ENTRADA",
            responsavel, reclamacao, dataCriacao, idPeca, idVeiculo, idCliente, idFuncionario, null,
            idOficina, false, false)));
    }

    /** Solicitação de revisão feita pelo próprio cliente, possivelmente para uma oficina diferente da sua. */
    public OrcamentoResponseDTO solicitar(double valor, String responsavel, String reclamacao,
                                          String dataCriacao, long idVeiculo, long idCliente,
                                          long idOficina, boolean levar, boolean buscar) {
        return service.paraDTO(service.criar(new OrcamentoRequestDTO(valor, "ENTRADA",
            responsavel, reclamacao, dataCriacao, null, idVeiculo, idCliente, null, null,
            idOficina, levar, buscar)));
    }

    public OrcamentoResponseDTO criarRevisao(long idServico, double valor, String responsavel,
                                             String reclamacao, long idVeiculo, long idCliente,
                                             Long idFuncionario, long idOficina) {
        return service.paraDTO(service.criarRevisao(idServico, valor, responsavel, reclamacao,
            idVeiculo, idCliente, idFuncionario, idOficina));
    }

    public void aprovar(long id) { service.aprovar(id); }
    public void reprovar(long id) { service.reprovar(id); }
    public void atualizarValor(long id, double valor) { service.atualizarValor(id, valor); }
    public boolean revogarAprovacaoSeNecessario(long id) { return service.revogarAprovacaoSeNecessario(id); }
    public List<OrcamentoResponseDTO> todos() { return service.listar(); }
    public List<OrcamentoResponseDTO> entrada() { return service.listarEntrada(); }
    public OrcamentoResponseDTO porId(long id) { return service.paraDTO(service.buscar(id)); }
}
