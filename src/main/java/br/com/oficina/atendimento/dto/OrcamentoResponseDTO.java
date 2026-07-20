package br.com.oficina.atendimento.dto;

public record OrcamentoResponseDTO(
    Long idOrcamento,
    double valor,
    String codigo,
    String tipo,
    String responsavel,
    String reclamacao,
    String dataCriacao,
    String status,
    Long idVeiculo,
    Long idCliente,
    Long idServicoRevisao
) {}
