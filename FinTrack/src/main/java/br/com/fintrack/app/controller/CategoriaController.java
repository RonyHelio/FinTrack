package br.com.fintrack.app.controller;

import br.com.fintrack.app.dto.CategoriaRequestDTO;
import br.com.fintrack.app.dto.CategoriaResponseDTO;
import br.com.fintrack.app.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    /**
     * Cria uma categoria personalizada para o usuário autenticado.
     * O usuarioId é extraído do JWT — nunca do body.
     */
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(
            @Valid @RequestBody CategoriaRequestDTO dto,
            Authentication authentication) {

        UUID usuarioId = (UUID) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.criar(usuarioId, dto));
    }

    /**
     * Lista as categorias visíveis para o usuário autenticado:
     * suas categorias personalizadas + as categorias globais (usuario_id = null).
     */
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listarMinhas(Authentication authentication) {
        UUID usuarioId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(categoriaService.listarPorUsuario(usuarioId));
    }

    /**
     * Lista apenas as categorias globais (usuario_id = null), acessível por qualquer usuário autenticado.
     */
    @GetMapping("/globais")
    public ResponseEntity<List<CategoriaResponseDTO>> listarGlobais() {
        return ResponseEntity.ok(categoriaService.listarGlobais());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    /**
     * Atualiza uma categoria personalizada.
     * Lança 403 se a categoria não pertencer ao usuário autenticado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CategoriaRequestDTO dto,
            Authentication authentication) {

        UUID usuarioId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(categoriaService.atualizar(id, usuarioId, dto));
    }

    /**
     * Exclui uma categoria personalizada.
     * Lança 403 se a categoria não pertencer ao usuário autenticado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID usuarioId = (UUID) authentication.getPrincipal();
        categoriaService.deletar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
