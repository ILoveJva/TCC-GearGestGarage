package br.com.oficina.atendimento.dto;

import java.util.List;

public record ServicoResponseDTO(
    Long idServico,
    String codigo,
    String titulo,
    String tipoServico,
    String tipoManutencao,
    String status,
    String dataServico,
    Long idVeiculo,
    Long idOrcamento,
    List<ItemView> itens
) {
    public record ItemView(Long id, int etapa, String codigo, String descricao, String status, String tempoGasto, Long idFuncionario) {}
}
