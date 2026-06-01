package br.com.fintrack.app.controller;

import br.com.fintrack.app.dto.DashboardResponseDTO;
import br.com.fintrack.app.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Resumo financeiro do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final TransacaoService transacaoService;

    @GetMapping
    @Operation(
            summary = "Obter dashboard financeiro",
            description = """
                    Retorna o resumo financeiro completo do mês corrente para o usuário autenticado:
                    
                    - **saldoTotal**: receitas históricas acumuladas − despesas históricas acumuladas
                    - **receitasMes**: soma de todas as receitas do mês atual
                    - **despesasMes**: soma de todas as despesas do mês atual
                    - **quantidadeTransacoesMes**: número total de lançamentos no mês atual
                    - **ultimas5Transacoes**: os 5 lançamentos mais recentes (qualquer tipo)
                    - **gastosPorCategoria**: despesas do mês agrupadas por categoria, ordenadas do maior ao menor valor
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dashboard calculado com sucesso",
                            content = @Content(schema = @Schema(implementation = DashboardResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
            }
    )
    public ResponseEntity<DashboardResponseDTO> getDashboard(Authentication authentication) {
        UUID usuarioId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(transacaoService.calcularDashboard(usuarioId));
    }
}
