package br.com.oficina.atendimento;

import br.com.oficina.atendimento.dto.*;
import br.com.oficina.shared.exception.RecursoNaoEncontradoException;
import java.util.ArrayList;
import java.util.List;

public class OrcamentoService {
    private final OrcamentoRepository repository;
    public OrcamentoService(OrcamentoRepository repository) { this.repository = repository; }

    public OrcamentoEntity criar(OrcamentoRequestDTO d) {
        OrcamentoEntity o = new OrcamentoEntity(null, d.valor(), "", d.tipo(),
            d.responsavel(), d.reclamacao(), d.dataCriacao(), "PENDENTE",
            d.idPeca(), d.idVeiculo(), d.idCliente(), d.idFuncionario(), d.idServicoRevisao());
        return repository.salvar(o);
    }

    /** Cria um orçamento interno (tipo=REVISAO) vinculado ao serviço pai. */
    public OrcamentoEntity criarRevisao(long idServico, double valor, String responsavel,
                                        String reclamacao, long idVeiculo, long idCliente,
                                        Long idFuncionario) {
        String hoje = java.time.LocalDate.now().toString();
        OrcamentoEntity o = new OrcamentoEntity(null, valor, "", "REVISAO",
            responsavel, reclamacao, hoje, "APROVADO",
            null, idVeiculo, idCliente, idFuncionario, idServico);
        OrcamentoEntity salvo = repository.salvar(o);
        repository.atualizarServicoRevisao(salvo.getIdOrcamento(), idServico);
        return salvo;
    }

    public OrcamentoEntity buscar(long id) {
        OrcamentoEntity o = repository.buscarPorId(id);
        if (o == null) throw new RecursoNaoEncontradoException("Orcamento " + id + " nao encontrado");
        return o;
    }

    public void aprovar(long id) { repository.atualizarStatus(id, "APROVADO"); }
    public void reprovar(long id) { repository.atualizarStatus(id, "REPROVADO"); }
    public void atualizarValor(long id, double valor) { repository.atualizarValor(id, valor); }

    /**
     * Ao editar um orcamento ja aprovado, ele volta a exigir aprovacao.
     * Retorna true se o status foi revogado (para o chamador congelar o servico vinculado).
     */
    public boolean revogarAprovacaoSeNecessario(long id) {
        OrcamentoEntity o = buscar(id);
        if (!"APROVADO".equalsIgnoreCase(o.getStatus())) return false;
        repository.atualizarStatus(id, "PENDENTE");
        return true;
    }

    public OrcamentoResponseDTO paraDTO(OrcamentoEntity o) {
        return new OrcamentoResponseDTO(o.getIdOrcamento(), o.getValor(), o.getCodigo(),
            o.getTipo(), o.getResponsavel(), o.getReclamacao(), o.getDataCriacao(), o.getStatus(),
            o.getIdVeiculo(), o.getIdCliente(), o.getIdServicoRevisao());
    }

    public List<OrcamentoResponseDTO> listar() {
        List<OrcamentoResponseDTO> out = new ArrayList<>();
        for (OrcamentoEntity o : repository.listarTodos()) out.add(paraDTO(o));
        return out;
    }

    /** Lista apenas orçamentos externos (tipo=ENTRADA). */
    public List<OrcamentoResponseDTO> listarEntrada() {
        List<OrcamentoResponseDTO> out = new ArrayList<>();
        for (OrcamentoEntity o : repository.listarPorTipo("ENTRADA")) out.add(paraDTO(o));
        return out;
    }
}
