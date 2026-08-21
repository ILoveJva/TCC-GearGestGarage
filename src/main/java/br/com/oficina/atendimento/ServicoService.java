package br.com.oficina.atendimento;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import br.com.oficina.shared.exception.RecursoNaoEncontradoException;
import br.com.oficina.shared.exception.RegraNegocioException;
import java.util.ArrayList;
import java.util.List;

public class ServicoService {
    private final ServicoRepository repository;
    public ServicoService(ServicoRepository repository) { this.repository = repository; }

    public ServicoEntity abrir(String titulo, String tipoServico, String tipoManutencao,
                               String dataServico, int km,
                               long idVeiculo, long idOficina, long idOrcamento) {
        ServicoEntity entity = new ServicoEntity(null, "", dataServico, km, titulo,
            tipoServico, "ABERTA", idVeiculo, idOficina, idOrcamento);
        entity.setTipoManutencao(tipoManutencao != null ? tipoManutencao : "CORRETIVA");
        ServicoEntity s = repository.salvar(entity);
        return repository.buscarPorId(s.getIdServico());
    }

    public ItemServicoEntity adicionarItem(long idServico, int etapa, String descricao,
                                           Long idPeca, Long idFuncionario) {
        return repository.salvarItem(new ItemServicoEntity(null, etapa, "", descricao, "PENDENTE",
            "", null, idPeca, idServico, idFuncionario));
    }

    /**
     * Registra a única etapa da OS: uma descrição geral + checklist de itens.
     * Cada itemChecklist é String[]{descricao, tempoGasto, "true"/"false" (realizado)}.
     * Move o serviço para EM_ANDAMENTO.
     */
    public void registrarEtapaOS(long idServico, String descricaoGeral, java.util.List<String[]> checklist) {
        String hoje = java.time.LocalDate.now().toString();
        // etapa 0 = descrição geral da OS
        repository.salvarItem(new ItemServicoEntity(null, 0, "", descricaoGeral, "CONCLUIDO",
            "", hoje, null, idServico, null));
        // etapa 1 = cada item do checklist
        if (checklist != null) {
            for (String[] row : checklist) {
                String descricao = row.length > 0 ? row[0] : "";
                String tempoGasto = row.length > 1 ? row[1] : "";
                String status = (row.length > 2 && "true".equalsIgnoreCase(row[2])) ? "CONCLUIDO" : "PENDENTE";
                if (!descricao.isBlank())
                    repository.salvarItem(new ItemServicoEntity(null, 1, "", descricao, status,
                        tempoGasto, hoje, null, idServico, null));
            }
        }
        repository.atualizarStatus(idServico, "EM_ANDAMENTO");
    }


    public ServicoEntity finalizar(long idServico, String observacaoSaida, Long idFuncionario, String data) {
        ServicoEntity s = buscar(idServico);
        if ("CONCLUIDA".equals(s.getStatus()))
            throw new RegraNegocioException("Servico " + idServico + " ja esta concluido");
        if (!"EM_ANDAMENTO".equals(s.getStatus()))
            throw new RegraNegocioException("Servico precisa estar em andamento para ser finalizado");
        repository.salvarItem(new ItemServicoEntity(null, 2, "", observacaoSaida, "CONCLUIDO",
            "", data, null, idServico, idFuncionario));
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
            itens.add(new ServicoResponseDTO.ItemView(i.getIdItemServico(), i.getEtapa(),
                i.getCodigo(), i.getDescricao(), i.getStatus(), i.getTempoGasto(), i.getIdFuncionario()));
        return new ServicoResponseDTO(s.getIdServico(), s.getCodigo(), s.getTitulo(),
            s.getTipoServico(), s.getTipoManutencao(), s.getStatus(), s.getDataServico(),
            s.getIdVeiculo(), s.getIdOficina(), s.getIdOrcamento(), itens);
    }

    public List<ServicoResponseDTO> listar() {
        List<ServicoResponseDTO> out = new ArrayList<>();
        for (ServicoEntity s : repository.listarTodos()) out.add(paraDTO(s));
        return out;
    }
}
