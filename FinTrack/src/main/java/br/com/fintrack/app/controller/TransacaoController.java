package br.com.fintrack.app.controller;

import br.com.fintrack.app.dto.TransacaoRequestDTO;
import br.com.fintrack.app.dto.TransacaoResponseDTO;
import br.com.fintrack.app.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<TransacaoResponseDTO> criar(@Valid @RequestBody TransacaoRequestDTO dto) {
        TransacaoResponseDTO response = transacaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TransacaoResponseDTO>> listarPorUsuario(@PathVariable UUID usuarioId) {
        List<TransacaoResponseDTO> transacoes = transacaoService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/usuario/{usuarioId}/periodo")
    public ResponseEntity<List<TransacaoResponseDTO>> listarPorPeriodo(
            @PathVariable UUID usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        List<TransacaoResponseDTO> transacoes = transacaoService.listarPorUsuarioEPeriodo(usuarioId, inicio, fim);
        return ResponseEntity.ok(transacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        TransacaoResponseDTO transacao = transacaoService.buscarPorId(id);
        return ResponseEntity.ok(transacao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody TransacaoRequestDTO dto) {
        TransacaoResponseDTO transacao = transacaoService.atualizar(id, dto);
        return ResponseEntity.ok(transacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        transacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
