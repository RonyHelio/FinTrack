package br.com.fintrack.app.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record GastoPorCategoriaDTO(
        UUID categoriaId,
        String categoriaNome,
        String categoriaIcone,
        BigDecimal totalGasto,
        long quantidadeTransacoes
) {}
