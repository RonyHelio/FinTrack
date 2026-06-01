package br.com.fintrack.app.controller;

import br.com.fintrack.app.dto.LoginRequestDTO;
import br.com.fintrack.app.dto.LoginResponseDTO;
import br.com.fintrack.app.dto.UsuarioRequestDTO;
import br.com.fintrack.app.dto.UsuarioResponseDTO;
import br.com.fintrack.app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints públicos de registro e login")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria um novo usuário no sistema. Retorna 409 se o e-mail já estiver cadastrado.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                            content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos")
            }
    )
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuário",
            description = "Valida as credenciais e retorna o JWT no campo 'token'. Use o token no header Authorization: Bearer {token}.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login bem-sucedido — token JWT retornado",
                            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos")
            }
    )
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
