package br.com.oficina.atendimento.dto;

public record OrcamentoResponseDTO(
    Long idOrcamento,
    double valor,
    Long idVeiculo,
    Long idCliente
) {}
