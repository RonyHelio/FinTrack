package br.com.fintrack.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransacaoResponseDTO(
        UUID id,
        UUID usuarioId,
        UUID categoriaId,
        String categoriaNome,
        BigDecimal valor,
        String tipo,
        LocalDate data,
        String descricao
) {}
