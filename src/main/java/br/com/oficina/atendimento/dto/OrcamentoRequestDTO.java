package br.com.oficina.atendimento.dto;

public record OrcamentoRequestDTO(
    double valor,
    Long idPeca,
    Long idVeiculo,
    Long idCliente,
    Long idFuncionario
) {}
