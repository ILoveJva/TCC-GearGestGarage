package br.com.oficina.atendimento;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import br.com.oficina.shared.exception.RecursoNaoEncontradoException;
import br.com.oficina.shared.exception.RegraNegocioException;
import java.util.ArrayList;
import java.util.List;

public class ServicoService {
    private final ServicoRepository repository;
    public ServicoService(ServicoRepository repository) { this.repository = repository; }

    /** Abre um servico (O.S.) a partir de um orcamento existente. */
    public ServicoEntity abrir(String titulo, String tipoServico, String dataServico, int km,
                               long idVeiculo, long idOficina, long idOrcamento) {
        ServicoEntity s = repository.salvar(new ServicoEntity(null, dataServico, km, titulo,
            tipoServico, "ABERTA", idVeiculo, idOficina, idOrcamento));
        return repository.buscarPorId(s.getIdServico());
    }

    public ItemServicoEntity adicionarItem(long idServico, String descricao, long idPeca, long idFuncionario) {
        return repository.salvarItem(new ItemServicoEntity(null, descricao, "PENDENTE", null,
            idPeca, idServico, idFuncionario));
    }

    public ServicoEntity realizar(long idServico, String data) {
        ServicoEntity s = buscar(idServico);
        if ("CONCLUIDA".equals(s.getStatus()))
            throw new RegraNegocioException("Servico " + idServico + " ja esta concluido");
        repository.concluirItens(idServico, data);
        repository.atualizarStatus(idServico, "CONCLUIDA");
        return repository.buscarPorId(idServico);
    }

    public ServicoEntity buscar(long id) {
        ServicoEntity s = repository.buscarPorId(id);
        if (s == null) throw new RecursoNaoEncontradoException("Servico " + id + " nao encontrado");
        return s;
    }

    public ServicoResponseDTO paraDTO(ServicoEntity s) {
        List<ServicoResponseDTO.ItemView> itens = new ArrayList<>();
        for (ItemServicoEntity i : s.getItens())
            itens.add(new ServicoResponseDTO.ItemView(i.getIdItemServico(), i.getDescricao(), i.getStatus()));
        return new ServicoResponseDTO(s.getIdServico(), s.getTitulo(), s.getTipoServico(),
            s.getStatus(), s.getDataServico(), s.getIdVeiculo(), itens);
    }

    public List<ServicoResponseDTO> listar() {
        List<ServicoResponseDTO> out = new ArrayList<>();
        for (ServicoEntity s : repository.listarTodos()) out.add(paraDTO(s));
        return out;
    }
}
