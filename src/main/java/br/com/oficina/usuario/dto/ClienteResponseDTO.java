package br.com.oficina.usuario.dto;

public record ClienteResponseDTO(
    Long idCliente,
    String nome,
    String email,
    String telefone,
    String cpf
) {}
