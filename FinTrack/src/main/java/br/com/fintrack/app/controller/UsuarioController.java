package br.com.fintrack.app.controller;

import br.com.fintrack.app.dto.LoginRequestDTO;
import br.com.fintrack.app.dto.LoginResponseDTO;
import br.com.fintrack.app.dto.UsuarioRequestDTO;
import br.com.fintrack.app.dto.UsuarioResponseDTO;
import br.com.fintrack.app.exception.UnauthorizedAccessException;
import br.com.fintrack.app.service.AuthService;
import br.com.fintrack.app.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    // ─── Auth (públicos) ──────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    // ─── Perfil (protegidos) ──────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    /**
     * Atualiza o perfil. O usuário só pode atualizar o próprio perfil.
     * O ID autenticado é extraído do JWT e comparado com o {id} da URL.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody UsuarioRequestDTO dto,
            Authentication authentication) {

        UUID usuarioAutenticadoId = (UUID) authentication.getPrincipal();
        if (!usuarioAutenticadoId.equals(id)) {
            throw new UnauthorizedAccessException("perfil de usuário");
        }

        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    /**
     * Exclui a conta. O usuário só pode excluir a própria conta.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID usuarioAutenticadoId = (UUID) authentication.getPrincipal();
        if (!usuarioAutenticadoId.equals(id)) {
            throw new UnauthorizedAccessException("conta de usuário");
        }

        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
