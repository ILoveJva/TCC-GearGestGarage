package br.com.oficina.atendimento.dto;

import java.util.List;

public record ServicoResponseDTO(
    Long idServico,
    String titulo,
    String tipoServico,
    String status,
    String dataServico,
    Long idVeiculo,
    List<ItemView> itens
) {
    public record ItemView(Long id, String descricao, String status) {}
}
