package br.com.oficina.atendimento;

import br.com.oficina.atendimento.dto.*;
import br.com.oficina.shared.exception.RecursoNaoEncontradoException;
import java.util.ArrayList;
import java.util.List;

public class OrcamentoService {
    private final OrcamentoRepository repository;
    public OrcamentoService(OrcamentoRepository repository) { this.repository = repository; }

    public OrcamentoEntity criar(OrcamentoRequestDTO d) {
        return repository.salvar(new OrcamentoEntity(null, d.valor(), d.idPeca(),
            d.idVeiculo(), d.idCliente(), d.idFuncionario()));
    }
    public OrcamentoEntity buscar(long id) {
        OrcamentoEntity o = repository.buscarPorId(id);
        if (o == null) throw new RecursoNaoEncontradoException("Orcamento " + id + " nao encontrado");
        return o;
    }
    public OrcamentoResponseDTO paraDTO(OrcamentoEntity o) {
        return new OrcamentoResponseDTO(o.getIdOrcamento(), o.getValor(), o.getIdVeiculo(), o.getIdCliente());
    }
    public List<OrcamentoResponseDTO> listar() {
        List<OrcamentoResponseDTO> out = new ArrayList<>();
        for (OrcamentoEntity o : repository.listarTodos()) out.add(paraDTO(o));
        return out;
    }
}
