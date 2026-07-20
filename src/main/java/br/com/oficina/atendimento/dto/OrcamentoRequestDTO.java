package br.com.oficina.atendimento.dto;

public record OrcamentoRequestDTO(
    double valor,
    String tipo,
    String responsavel,
    String reclamacao,
    String dataCriacao,
    Long idPeca,
    Long idVeiculo,
    Long idCliente,
    Long idFuncionario,
    Long idServicoRevisao
) {}
