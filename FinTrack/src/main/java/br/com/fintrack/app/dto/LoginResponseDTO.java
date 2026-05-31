package br.com.fintrack.app.dto;

import java.util.UUID;

public record LoginResponseDTO(
        UUID id,
        String nome,
        String email,
        String token,
        String mensagem
) {}
