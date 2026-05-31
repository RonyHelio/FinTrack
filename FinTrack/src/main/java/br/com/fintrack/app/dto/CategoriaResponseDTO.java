package br.com.fintrack.app.dto;

import java.util.UUID;

public record CategoriaResponseDTO(
        UUID id,
        UUID usuarioId,
        String nome,
        String icone
) {}
