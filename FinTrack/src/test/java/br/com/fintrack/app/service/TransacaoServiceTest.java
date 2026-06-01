package br.com.fintrack.app.service;

import br.com.fintrack.app.dto.DashboardResponseDTO;
import br.com.fintrack.app.dto.TransacaoRequestDTO;
import br.com.fintrack.app.dto.TransacaoResponseDTO;
import br.com.fintrack.app.entity.Categoria;
import br.com.fintrack.app.entity.TipoTransacao;
import br.com.fintrack.app.entity.Transacao;
import br.com.fintrack.app.entity.Usuario;
import br.com.fintrack.app.exception.ResourceNotFoundException;
import br.com.fintrack.app.exception.UnauthorizedAccessException;
import br.com.fintrack.app.repository.CategoriaRepository;
import br.com.fintrack.app.repository.TransacaoRepository;
import br.com.fintrack.app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransacaoService — testes unitários")
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    // ─── Dados compartilhados ─────────────────────────────────────────────────

    private UUID usuarioId;
    private UUID outroUsuarioId;
    private UUID transacaoId;
    private UUID categoriaId;
    private Usuario usuarioEntity;
    private Categoria categoriaEntity;
    private Transacao transacaoDespesa;
    private Transacao transacaoReceita;
    private TransacaoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        outroUsuarioId = UUID.randomUUID();
        transacaoId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();

        usuarioEntity = Usuario.builder()
                .id(usuarioId)
                .nome("Ana Costa")
                .email("ana@email.com")
                .senha("senha")
                .build();

        categoriaEntity = Categoria.builder()
                .id(categoriaId)
                .nome("Alimentação")
                .icone("🍔")
                .usuario(null)
                .build();

        transacaoDespesa = Transacao.builder()
                .id(transacaoId)
                .usuario(usuarioEntity)
                .categoria(categoriaEntity)
                .valor(new BigDecimal("150.00"))
                .tipo(TipoTransacao.DESPESA)
                .data(LocalDate.now())
                .descricao("Supermercado")
                .build();

        transacaoReceita = Transacao.builder()
                .id(UUID.randomUUID())
                .usuario(usuarioEntity)
                .categoria(categoriaEntity)
                .valor(new BigDecimal("3000.00"))
                .tipo(TipoTransacao.RECEITA)
                .data(LocalDate.now())
                .descricao("Salário")
                .build();

        requestDTO = new TransacaoRequestDTO(
                categoriaId,
                new BigDecimal("150.00"),
                "despesa",
                LocalDate.now(),
                "Supermercado"
        );
    }

    // ─── criar() ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("criar()")
    class CriarTests {

        @Test
        @DisplayName("deve criar transação e retornar DTO com dados corretos")
        void deveCriarComSucesso() {
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEntity));
            when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoriaEntity));
            when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacaoDespesa);

            TransacaoResponseDTO response = transacaoService.criar(usuarioId, requestDTO);

            assertThat(response).isNotNull();
            assertThat(response.usuarioId()).isEqualTo(usuarioId);
            assertThat(response.valor()).isEqualByComparingTo("150.00");
            assertThat(response.tipo()).isEqualTo("despesa");

            verify(transacaoRepository).save(any(Transacao.class));
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando usuário não existe")
        void deveLancarExcecaoQuandoUsuarioNaoExiste() {
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transacaoService.criar(usuarioId, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando categoria não existe")
        void deveLancarExcecaoQuandoCategoriaNaoExiste() {
            when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioEntity));
            when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transacaoService.criar(usuarioId, requestDTO))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(transacaoRepository, never()).save(any());
        }
    }

    // ─── Isolamento Tenant ────────────────────────────────────────────────────

    @Nested
    @DisplayName("Isolamento Tenant — regra de segurança")
    class TenantIsolationTests {

        @Test
        @DisplayName("buscarPorId deve lançar UnauthorizedAccessException quando a transação é de outro usuário")
        void buscarPorId_deveLancarForbiddenParaOutroUsuario() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacaoDespesa));

            assertThatThrownBy(() -> transacaoService.buscarPorId(transacaoId, outroUsuarioId))
                    .isInstanceOf(UnauthorizedAccessException.class);
        }

        @Test
        @DisplayName("atualizar deve lançar UnauthorizedAccessException quando a transação é de outro usuário")
        void atualizar_deveLancarForbiddenParaOutroUsuario() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacaoDespesa));

            assertThatThrownBy(() -> transacaoService.atualizar(transacaoId, outroUsuarioId, requestDTO))
                    .isInstanceOf(UnauthorizedAccessException.class);

            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("deletar deve lançar UnauthorizedAccessException quando a transação é de outro usuário")
        void deletar_deveLancarForbiddenParaOutroUsuario() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacaoDespesa));

            assertThatThrownBy(() -> transacaoService.deletar(transacaoId, outroUsuarioId))
                    .isInstanceOf(UnauthorizedAccessException.class);

            verify(transacaoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("buscarPorId deve retornar DTO quando a transação pertence ao usuário autenticado")
        void buscarPorId_deveRetornarDTOParaDono() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacaoDespesa));

            TransacaoResponseDTO response = transacaoService.buscarPorId(transacaoId, usuarioId);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(transacaoId);
        }
    }

    // ─── deletar() ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deletar()")
    class DeletarTests {

        @Test
        @DisplayName("deve deletar transação quando o usuário é o dono")
        void deveDeletarComSucesso() {
            when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacaoDespesa));

            transacaoService.deletar(transacaoId, usuarioId);

            verify(transacaoRepository).delete(transacaoDespesa);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando transação não existe")
        void deveLancarExcecaoQuandoNaoExiste() {
            UUID inexistente = UUID.randomUUID();
            when(transacaoRepository.findById(inexistente)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transacaoService.deletar(inexistente, usuarioId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── calcularDashboard() ─────────────────────────────────────────────────

    @Nested
    @DisplayName("calcularDashboard()")
    class DashboardTests {

        @Test
        @DisplayName("deve retornar dashboard com saldo, receitas, despesas e listas corretos")
        void deveCalcularDashboardCorretamente() {
            YearMonth mesAtual = YearMonth.now();
            LocalDate inicio = mesAtual.atDay(1);
            LocalDate fim = mesAtual.atEndOfMonth();

            // Mocks para somas do mês
            when(transacaoRepository.sumValorByUsuarioIdAndTipoAndDataBetween(
                    eq(usuarioId), eq(TipoTransacao.RECEITA), eq(inicio), eq(fim)))
                    .thenReturn(new BigDecimal("3000.00"));

            when(transacaoRepository.sumValorByUsuarioIdAndTipoAndDataBetween(
                    eq(usuarioId), eq(TipoTransacao.DESPESA), eq(inicio), eq(fim)))
                    .thenReturn(new BigDecimal("150.00"));

            // Mocks para saldo histórico
            when(transacaoRepository.findByUsuarioIdAndTipo(usuarioId, TipoTransacao.RECEITA))
                    .thenReturn(List.of(transacaoReceita));

            when(transacaoRepository.findByUsuarioIdAndTipo(usuarioId, TipoTransacao.DESPESA))
                    .thenReturn(List.of(transacaoDespesa));

            // Mock para contagem do mês
            when(transacaoRepository.countByUsuarioIdAndDataBetween(eq(usuarioId), eq(inicio), eq(fim)))
                    .thenReturn(2L);

            // Mock para últimas 5
            when(transacaoRepository.findTop5ByUsuarioIdOrderByDataDescIdDesc(usuarioId))
                    .thenReturn(List.of(transacaoDespesa, transacaoReceita));

            // Mock para despesas por categoria
            when(transacaoRepository.findDespesasByUsuarioIdAndDataBetween(
                    eq(usuarioId), eq(inicio), eq(fim)))
                    .thenReturn(List.of(transacaoDespesa));

            DashboardResponseDTO dashboard = transacaoService.calcularDashboard(usuarioId);

            assertThat(dashboard).isNotNull();
            // saldo = 3000 (receita) - 150 (despesa) = 2850
            assertThat(dashboard.saldoTotal()).isEqualByComparingTo("2850.00");
            assertThat(dashboard.receitasMes()).isEqualByComparingTo("3000.00");
            assertThat(dashboard.despesasMes()).isEqualByComparingTo("150.00");
            assertThat(dashboard.quantidadeTransacoesMes()).isEqualTo(2L);
            assertThat(dashboard.ultimas5Transacoes()).hasSize(2);
            assertThat(dashboard.gastosPorCategoria()).hasSize(1);
            assertThat(dashboard.gastosPorCategoria().get(0).totalGasto())
                    .isEqualByComparingTo("150.00");
        }

        @Test
        @DisplayName("deve retornar saldo zero e listas vazias quando usuário não tem transações")
        void deveRetornarZerosQuandoSemTransacoes() {
            YearMonth mesAtual = YearMonth.now();
            LocalDate inicio = mesAtual.atDay(1);
            LocalDate fim = mesAtual.atEndOfMonth();

            when(transacaoRepository.sumValorByUsuarioIdAndTipoAndDataBetween(
                    any(), any(), any(), any()))
                    .thenReturn(BigDecimal.ZERO);

            when(transacaoRepository.findByUsuarioIdAndTipo(any(), any()))
                    .thenReturn(List.of());

            when(transacaoRepository.countByUsuarioIdAndDataBetween(any(), any(), any()))
                    .thenReturn(0L);

            when(transacaoRepository.findTop5ByUsuarioIdOrderByDataDescIdDesc(any()))
                    .thenReturn(List.of());

            when(transacaoRepository.findDespesasByUsuarioIdAndDataBetween(any(), any(), any()))
                    .thenReturn(List.of());

            DashboardResponseDTO dashboard = transacaoService.calcularDashboard(usuarioId);

            assertThat(dashboard.saldoTotal()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(dashboard.receitasMes()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(dashboard.despesasMes()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(dashboard.quantidadeTransacoesMes()).isZero();
            assertThat(dashboard.ultimas5Transacoes()).isEmpty();
            assertThat(dashboard.gastosPorCategoria()).isEmpty();
        }
    }
}
