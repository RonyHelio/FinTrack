package br.com.fintrack.app.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponseDTO(
        BigDecimal saldoTotal,
        BigDecimal receitasMes,
        BigDecimal despesasMes,
        long quantidadeTransacoesMes,
        List<TransacaoResponseDTO> ultimas5Transacoes,
        List<GastoPorCategoriaDTO> gastosPorCategoria
) {}
