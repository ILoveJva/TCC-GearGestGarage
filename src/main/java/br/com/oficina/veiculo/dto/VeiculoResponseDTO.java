package br.com.oficina.veiculo.dto;

public record VeiculoResponseDTO(
    Long idVeiculo,
    String tipo,
    String placa,
    int ano,
    String montadora,
    String modelo,
    Long idCliente,
    String proprietario
) {}
