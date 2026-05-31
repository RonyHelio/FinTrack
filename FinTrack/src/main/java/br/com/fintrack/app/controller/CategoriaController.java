package br.com.fintrack.app.controller;

import br.com.fintrack.app.dto.CategoriaRequestDTO;
import br.com.fintrack.app.dto.CategoriaResponseDTO;
import br.com.fintrack.app.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criar(@Valid @RequestBody CategoriaRequestDTO dto) {
        CategoriaResponseDTO response = categoriaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CategoriaResponseDTO>> listarPorUsuario(@PathVariable UUID usuarioId) {
        List<CategoriaResponseDTO> categorias = categoriaService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/globais")
    public ResponseEntity<List<CategoriaResponseDTO>> listarGlobais() {
        List<CategoriaResponseDTO> categorias = categoriaService.listarGlobais();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable UUID id) {
        CategoriaResponseDTO categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody CategoriaRequestDTO dto) {
        CategoriaResponseDTO categoria = categoriaService.atualizar(id, dto);
        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
