package br.com.fintrack.app.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CategoriaRequestDTO(
        UUID usuarioId,

        @NotBlank(message = "Nome da categoria é obrigatório")
        String nome,

        String icone
) {}
