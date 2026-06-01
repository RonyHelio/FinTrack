package br.com.fintrack.app.controller;

import br.com.fintrack.app.dto.TransacaoRequestDTO;
import br.com.fintrack.app.dto.TransacaoResponseDTO;
import br.com.fintrack.app.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "CRUD de transações financeiras com isolamento por usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class TransacaoController {

    private final TransacaoService transacaoService;

    @PostMapping
    @Operation(summary = "Criar transação", description = "Registra uma nova receita ou despesa para o usuário autenticado.")
    public ResponseEntity<TransacaoResponseDTO> criar(
            @Valid @RequestBody TransacaoRequestDTO dto,
            Authentication authentication) {

        UUID usuarioId = (UUID) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(transacaoService.criar(usuarioId, dto));
    }

    @GetMapping
    @Operation(summary = "Listar todas as transações", description = "Retorna todas as transações do usuário autenticado, ordenadas por data descendente.")
    public ResponseEntity<List<TransacaoResponseDTO>> listarMinhas(Authentication authentication) {
        UUID usuarioId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(transacaoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/periodo")
    @Operation(summary = "Listar por período", description = "Retorna as transações do usuário em um período específico.")
    public ResponseEntity<List<TransacaoResponseDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            Authentication authentication) {

        UUID usuarioId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(transacaoService.listarPorPeriodo(usuarioId, inicio, fim));
    }

    @GetMapping("/filtro")
    @Operation(
            summary = "Filtrar transações",
            description = """
                    Retorna transações do usuário autenticado aplicando filtros combinados e opcionais.
                    Todos os parâmetros são opcionais — omitir = sem restrição.
                    
                    Exemplos:
                    - `?mes=5&ano=2026` → transações de maio/2026
                    - `?ano=2026&tipo=despesa` → todas as despesas de 2026
                    - `?categoriaId=uuid&tipo=despesa` → despesas de uma categoria específica
                    - `?mes=5&ano=2026&categoriaId=uuid&tipo=despesa` → filtro completo
                    """
    )
    public ResponseEntity<List<TransacaoResponseDTO>> filtrar(
            @Parameter(description = "Mês (1–12)", example = "5")
            @RequestParam(required = false) Integer mes,

            @Parameter(description = "Ano (ex: 2026)", example = "2026")
            @RequestParam(required = false) Integer ano,

            @Parameter(description = "UUID da categoria")
            @RequestParam(required = false) UUID categoriaId,

            @Parameter(description = "Tipo: 'receita' ou 'despesa'", example = "despesa")
            @RequestParam(required = false) String tipo,

            Authentication authentication) {

        UUID usuarioId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(transacaoService.filtrar(usuarioId, mes, ano, categoriaId, tipo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna uma transação específica. Lança 403 se não pertencer ao usuário autenticado.")
    public ResponseEntity<TransacaoResponseDTO> buscarPorId(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID usuarioId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(transacaoService.buscarPorId(id, usuarioId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar transação", description = "Atualiza uma transação. Lança 403 se não pertencer ao usuário autenticado.")
    public ResponseEntity<TransacaoResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody TransacaoRequestDTO dto,
            Authentication authentication) {

        UUID usuarioId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(transacaoService.atualizar(id, usuarioId, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir transação", description = "Exclui uma transação. Lança 403 se não pertencer ao usuário autenticado.")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID usuarioId = (UUID) authentication.getPrincipal();
        transacaoService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
