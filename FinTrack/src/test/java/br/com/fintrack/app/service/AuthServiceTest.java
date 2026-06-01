package br.com.fintrack.app.service;

import br.com.fintrack.app.dto.LoginRequestDTO;
import br.com.fintrack.app.dto.LoginResponseDTO;
import br.com.fintrack.app.dto.UsuarioRequestDTO;
import br.com.fintrack.app.dto.UsuarioResponseDTO;
import br.com.fintrack.app.entity.Usuario;
import br.com.fintrack.app.exception.DuplicateEmailException;
import br.com.fintrack.app.exception.InvalidLoginException;
import br.com.fintrack.app.repository.UsuarioRepository;
import br.com.fintrack.app.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — testes unitários")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // ─── Dados compartilhados ─────────────────────────────────────────────────

    private UUID usuarioId;
    private Usuario usuarioEntity;
    private UsuarioRequestDTO registrarDTO;
    private LoginRequestDTO loginDTO;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();

        usuarioEntity = Usuario.builder()
                .id(usuarioId)
                .nome("Maria Silva")
                .email("maria@email.com")
                .senha("senha123")
                .dataCadastro(LocalDateTime.now())
                .build();

        registrarDTO = new UsuarioRequestDTO("Maria Silva", "maria@email.com", "senha123");
        loginDTO = new LoginRequestDTO("maria@email.com", "senha123");
    }

    // ─── registrar() ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("registrar()")
    class RegistrarTests {

        @Test
        @DisplayName("deve retornar UsuarioResponseDTO quando os dados são válidos")
        void deveRegistrarComSucesso() {
            when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(false);
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEntity);

            UsuarioResponseDTO response = authService.registrar(registrarDTO);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(usuarioId);
            assertThat(response.nome()).isEqualTo("Maria Silva");
            assertThat(response.email()).isEqualTo("maria@email.com");

            verify(usuarioRepository).existsByEmail("maria@email.com");
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("deve lançar DuplicateEmailException quando e-mail já está cadastrado")
        void deveLancarExcecaoQuandoEmailDuplicado() {
            when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.registrar(registrarDTO))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessageContaining("maria@email.com");

            verify(usuarioRepository).existsByEmail("maria@email.com");
            verify(usuarioRepository, never()).save(any());
        }
    }

    // ─── login() ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("deve retornar LoginResponseDTO com token quando credenciais são válidas")
        void deveLoginComSucesso() {
            when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(usuarioEntity));
            when(jwtService.generateToken(usuarioId, "maria@email.com")).thenReturn("jwt.token.gerado");

            LoginResponseDTO response = authService.login(loginDTO);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(usuarioId);
            assertThat(response.email()).isEqualTo("maria@email.com");
            assertThat(response.token()).isEqualTo("jwt.token.gerado");
            assertThat(response.mensagem()).isEqualTo("Login realizado com sucesso");

            verify(jwtService).generateToken(usuarioId, "maria@email.com");
        }

        @Test
        @DisplayName("deve lançar InvalidLoginException quando o e-mail não é encontrado")
        void deveLancarExcecaoQuandoEmailNaoEncontrado() {
            when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(loginDTO))
                    .isInstanceOf(InvalidLoginException.class);

            verify(jwtService, never()).generateToken(any(), any());
        }

        @Test
        @DisplayName("deve lançar InvalidLoginException quando a senha está incorreta")
        void deveLancarExcecaoQuandoSenhaIncorreta() {
            when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(usuarioEntity));

            LoginRequestDTO loginErrado = new LoginRequestDTO("maria@email.com", "senha_errada");

            assertThatThrownBy(() -> authService.login(loginErrado))
                    .isInstanceOf(InvalidLoginException.class);

            verify(jwtService, never()).generateToken(any(), any());
        }
    }
}
