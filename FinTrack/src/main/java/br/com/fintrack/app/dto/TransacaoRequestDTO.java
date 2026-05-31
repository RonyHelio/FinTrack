package br.com.fintrack.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransacaoRequestDTO(
        @NotNull(message = "ID do usuário é obrigatório")
        UUID usuarioId,

        @NotNull(message = "ID da categoria é obrigatório")
        UUID categoriaId,

        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser positivo")
        BigDecimal valor,

        @NotBlank(message = "Tipo é obrigatório")
        String tipo,

        @NotNull(message = "Data é obrigatória")
        LocalDate data,

        String descricao
) {}
