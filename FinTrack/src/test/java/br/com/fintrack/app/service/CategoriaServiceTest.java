package br.com.fintrack.app.service;

import br.com.fintrack.app.dto.CategoriaRequestDTO;
import br.com.fintrack.app.dto.CategoriaResponseDTO;
import br.com.fintrack.app.entity.Categoria;
import br.com.fintrack.app.entity.Usuario;
import br.com.fintrack.app.exception.BusinessException;
import br.com.fintrack.app.exception.ResourceNotFoundException;
import br.com.fintrack.app.exception.UnauthorizedAccessException;
import br.com.fintrack.app.repository.CategoriaRepository;
import br.com.fintrack.app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaService — testes unitários")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    // ─── Dados compartilhados ─────────────────────────────────────────────────

    private UUID usuarioId;
    private UUID outrousuarioId;
    private UUID categoriaId;
    private Usuario usuarioEntity;
    private Categoria categoriaDoUsuario;
    private Categoria categoriaGlobal;
    private Categoria categoriaDeOutroUsuario;
    private CategoriaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        outrousuarioId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();

        usuarioEntity = Usuario.builder()
                .id(usuarioId)
                .nome("João")
                .email("joao@email.com")
                .senha("senha")
                .build();

        categoriaDoUsuario = Categoria.builder()
                .id(categoriaId)
                .usuario(usuarioEntity)
                .nome("Lazer")
                .icone("🎮")
                .build();

        categoriaGlobal = Categoria.builder()
                .id(UUID.randomUUID())
                .usuario(null) // global
                .nome("Alimentação")
                .icone("🍔")
                .build();

        Usuario outroUsuario = Usuario.builder().id(outrousuarioId).build();
        categoriaDeOutroUsuario = Categoria.builder()
                .id(UUID.randomUUID())
                .usuario(outroUsuario)
                .nome("Academia")
                .icone("💪")
                .build();

        requestDTO = new CategoriaRequestDTO("Lazer", "🎮");
    }

    // ─── criar() ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("criar()")
    class CriarTests {

        @Test
        @DisplayName("deve criar categoria personalizada e retornar DTO")
        void deveCriarComSucesso() {
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEntity));
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaDoUsuario);

            CategoriaResponseDTO response = categoriaService.criar(usuarioId, requestDTO);

            assertThat(response).isNotNull();
            assertThat(response.nome()).isEqualTo("Lazer");
            assertThat(response.usuarioId()).isEqualTo(usuarioId);

            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando usuário não existe")
        void deveLancarExcecaoQuandoUsuarioNaoExiste() {
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoriaService.criar(usuarioId, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(categoriaRepository, never()).save(any());
        }
    }

    // ─── listarPorUsuario() ───────────────────────────────────────────────────

    @Nested
    @DisplayName("listarPorUsuario()")
    class ListarTests {

        @Test
        @DisplayName("deve retornar categorias do usuário + globais")
        void deveListarCategoriasDoUsuarioEGlobais() {
            when(categoriaRepository.findByUsuarioIdOrUsuarioIsNull(usuarioId))
                    .thenReturn(List.of(categoriaDoUsuario, categoriaGlobal));

            List<CategoriaResponseDTO> resultado = categoriaService.listarPorUsuario(usuarioId);

            assertThat(resultado).hasSize(2);
        }
    }

    // ─── atualizar() ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("atualizar()")
    class AtualizarTests {

        @Test
        @DisplayName("deve atualizar categoria do próprio usuário com sucesso")
        void deveAtualizarComSucesso() {
            when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaDoUsuario));
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaDoUsuario);

            CategoriaResponseDTO response = categoriaService.atualizar(categoriaId, usuarioId, requestDTO);

            assertThat(response).isNotNull();
            verify(categoriaRepository).save(any(Categoria.class));
        }

        @Test
        @DisplayName("deve lançar BusinessException ao tentar editar categoria global")
        void deveLancarExcecaoQuandoCategoriaGlobal() {
            when(categoriaRepository.findById(categoriaGlobal.getId()))
                    .thenReturn(Optional.of(categoriaGlobal));

            assertThatThrownBy(() -> categoriaService.atualizar(categoriaGlobal.getId(), usuarioId, requestDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("globais");

            verify(categoriaRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar UnauthorizedAccessException ao tentar editar categoria de outro usuário")
        void deveLancarExcecaoQuandoCategoriaDeOutroUsuario() {
            when(categoriaRepository.findById(categoriaDeOutroUsuario.getId()))
                    .thenReturn(Optional.of(categoriaDeOutroUsuario));

            assertThatThrownBy(() ->
                    categoriaService.atualizar(categoriaDeOutroUsuario.getId(), usuarioId, requestDTO))
                    .isInstanceOf(UnauthorizedAccessException.class);

            verify(categoriaRepository, never()).save(any());
        }
    }

    // ─── deletar() ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deletar()")
    class DeletarTests {

        @Test
        @DisplayName("deve deletar categoria do próprio usuário com sucesso")
        void deveDeletarComSucesso() {
            when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaDoUsuario));

            categoriaService.deletar(categoriaId, usuarioId);

            verify(categoriaRepository).delete(categoriaDoUsuario);
        }

        @Test
        @DisplayName("deve lançar BusinessException ao tentar deletar categoria global")
        void deveLancarExcecaoQuandoCategoriaGlobal() {
            when(categoriaRepository.findById(categoriaGlobal.getId()))
                    .thenReturn(Optional.of(categoriaGlobal));

            assertThatThrownBy(() -> categoriaService.deletar(categoriaGlobal.getId(), usuarioId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("globais");

            verify(categoriaRepository, never()).delete(any());
        }

        @Test
        @DisplayName("deve lançar UnauthorizedAccessException ao tentar deletar categoria de outro usuário")
        void deveLancarExcecaoQuandoCategoriaDeOutroUsuario() {
            when(categoriaRepository.findById(categoriaDeOutroUsuario.getId()))
                    .thenReturn(Optional.of(categoriaDeOutroUsuario));

            assertThatThrownBy(() ->
                    categoriaService.deletar(categoriaDeOutroUsuario.getId(), usuarioId))
                    .isInstanceOf(UnauthorizedAccessException.class);

            verify(categoriaRepository, never()).delete(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando categoria não existe")
        void deveLancarExcecaoQuandoCategoriaNaoExiste() {
            UUID inexistente = UUID.randomUUID();
            when(categoriaRepository.findById(inexistente)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoriaService.deletar(inexistente, usuarioId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
