package br.com.oficina.estoque.dto;

public record PecaResponseDTO(
    Long idPeca,
    String nome,
    String vidaUtilKm,
    String vidaUtilTempo
) {}
