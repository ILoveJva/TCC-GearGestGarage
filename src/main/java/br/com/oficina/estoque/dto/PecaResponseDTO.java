package br.com.oficina.estoque.dto;

public record PecaResponseDTO(
    Long idPeca,
    String nome,
    String fabricante,
    String vidaUtilKm,
    String vidaUtilTempo
) {}
