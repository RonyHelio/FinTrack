package br.com.fintrack.app.service;

import br.com.fintrack.app.dto.DashboardResponseDTO;
import br.com.fintrack.app.dto.GastoPorCategoriaDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    /**
     * Cria uma transação para o usuário autenticado.
     * O usuarioId vem do JWT, não do body da requisição.
     */
    @Transactional
    public TransacaoResponseDTO criar(UUID usuarioAutenticadoId, TransacaoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioAutenticadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", usuarioAutenticadoId));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", dto.categoriaId()));

        TipoTransacao tipo = TipoTransacao.fromValor(dto.tipo());

        Transacao transacao = Transacao.builder()
                .usuario(usuario)
                .categoria(categoria)
                .valor(dto.valor())
                .tipo(tipo)
                .data(dto.data())
                .descricao(dto.descricao())
                .build();

        return toResponseDTO(transacaoRepository.save(transacao));
    }

    /**
     * Lista todas as transações do usuário autenticado, ordenadas por data descendente.
     */
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> listarPorUsuario(UUID usuarioAutenticadoId) {
        return transacaoRepository.findByUsuarioIdOrderByDataDesc(usuarioAutenticadoId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Lista transações do usuário autenticado em um período.
     */
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> listarPorPeriodo(UUID usuarioAutenticadoId, LocalDate inicio, LocalDate fim) {
        return transacaoRepository.findByUsuarioIdAndDataBetweenOrderByDataDesc(usuarioAutenticadoId, inicio, fim)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Filtra transações com parâmetros opcionais.
     *
     * @param mes         1–12 (null = qualquer mês)
     * @param ano         ex: 2026 (null = qualquer ano)
     * @param categoriaId UUID da categoria (null = qualquer categoria)
     * @param tipoStr     "receita" ou "despesa" (null = ambos)
     */
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> filtrar(
            UUID usuarioAutenticadoId,
            Integer mes,
            Integer ano,
            UUID categoriaId,
            String tipoStr) {

        LocalDate inicio = null;
        LocalDate fim = null;

        if (mes != null && ano != null) {
            YearMonth ym = YearMonth.of(ano, mes);
            inicio = ym.atDay(1);
            fim = ym.atEndOfMonth();
        } else if (ano != null) {
            inicio = LocalDate.of(ano, 1, 1);
            fim = LocalDate.of(ano, 12, 31);
        } else if (mes != null) {
            int anoAtual = LocalDate.now().getYear();
            YearMonth ym = YearMonth.of(anoAtual, mes);
            inicio = ym.atDay(1);
            fim = ym.atEndOfMonth();
        }

        TipoTransacao tipo = null;
        if (tipoStr != null && !tipoStr.isBlank()) {
            tipo = TipoTransacao.fromValor(tipoStr);
        }

        return transacaoRepository
                .findByFiltros(usuarioAutenticadoId, inicio, fim, categoriaId, tipo)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Busca uma transação específica, garantindo que pertence ao usuário autenticado.
     *
     * @throws ResourceNotFoundException se a transação não existir
     * @throws UnauthorizedAccessException se a transação pertencer a outro usuário
     */
    @Transactional(readOnly = true)
    public TransacaoResponseDTO buscarPorId(UUID id, UUID usuarioAutenticadoId) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação", "id", id));

        validarProprietario(transacao, usuarioAutenticadoId);

        return toResponseDTO(transacao);
    }

    /**
     * Atualiza uma transação, verificando que o usuário autenticado é o dono.
     *
     * @throws UnauthorizedAccessException se tentar editar transação de outro usuário
     */
    @Transactional
    public TransacaoResponseDTO atualizar(UUID id, UUID usuarioAutenticadoId, TransacaoRequestDTO dto) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação", "id", id));

        validarProprietario(transacao, usuarioAutenticadoId);

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", dto.categoriaId()));

        transacao.setCategoria(categoria);
        transacao.setValor(dto.valor());
        transacao.setTipo(TipoTransacao.fromValor(dto.tipo()));
        transacao.setData(dto.data());
        transacao.setDescricao(dto.descricao());

        return toResponseDTO(transacaoRepository.save(transacao));
    }

    /**
     * Exclui uma transação, verificando que o usuário autenticado é o dono.
     *
     * @throws UnauthorizedAccessException se tentar excluir transação de outro usuário
     */
    @Transactional
    public void deletar(UUID id, UUID usuarioAutenticadoId) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação", "id", id));

        validarProprietario(transacao, usuarioAutenticadoId);

        transacaoRepository.delete(transacao);
    }

    // ─── Dashboard ────────────────────────────────────────────────────────────

    /**
     * Calcula o dashboard financeiro completo para o usuário autenticado.
     * Todas as métricas de mês referem-se ao mês corrente (ano/mês atual).
     */
    @Transactional(readOnly = true)
    public DashboardResponseDTO calcularDashboard(UUID usuarioAutenticadoId) {
        YearMonth mesAtual = YearMonth.now();
        LocalDate inicioMes = mesAtual.atDay(1);
        LocalDate fimMes = mesAtual.atEndOfMonth();

        // Receitas e despesas do mês
        BigDecimal receitasMes = transacaoRepository.sumValorByUsuarioIdAndTipoAndDataBetween(
                usuarioAutenticadoId, TipoTransacao.RECEITA, inicioMes, fimMes);

        BigDecimal despesasMes = transacaoRepository.sumValorByUsuarioIdAndTipoAndDataBetween(
                usuarioAutenticadoId, TipoTransacao.DESPESA, inicioMes, fimMes);

        // Saldo total histórico (todas as transações, não só do mês)
        BigDecimal totalReceitas = transacaoRepository
                .findByUsuarioIdAndTipo(usuarioAutenticadoId, TipoTransacao.RECEITA)
                .stream()
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = transacaoRepository
                .findByUsuarioIdAndTipo(usuarioAutenticadoId, TipoTransacao.DESPESA)
                .stream()
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoTotal = totalReceitas.subtract(totalDespesas);

        // Quantidade de transações no mês
        long quantidadeTransacoesMes = transacaoRepository
                .countByUsuarioIdAndDataBetween(usuarioAutenticadoId, inicioMes, fimMes);

        // Últimas 5 transações (qualquer tipo, mais recentes)
        List<TransacaoResponseDTO> ultimas5 = transacaoRepository
                .findTop5ByUsuarioIdOrderByDataDescIdDesc(usuarioAutenticadoId)
                .stream()
                .map(this::toResponseDTO)
                .toList();

        // Gastos por categoria (apenas despesas do mês corrente)
        List<GastoPorCategoriaDTO> gastosPorCategoria = calcularGastosPorCategoria(
                usuarioAutenticadoId, inicioMes, fimMes);

        return new DashboardResponseDTO(
                saldoTotal,
                receitasMes,
                despesasMes,
                quantidadeTransacoesMes,
                ultimas5,
                gastosPorCategoria
        );
    }

    // ─── Helpers privados ─────────────────────────────────────────────────────

    /**
     * Agrupa as despesas do mês por categoria, ordenando do maior gasto para o menor.
     */
    private List<GastoPorCategoriaDTO> calcularGastosPorCategoria(
            UUID usuarioAutenticadoId, LocalDate inicio, LocalDate fim) {

        List<Transacao> despesasMes = transacaoRepository
                .findDespesasByUsuarioIdAndDataBetween(usuarioAutenticadoId, inicio, fim);

        Map<UUID, List<Transacao>> porCategoria = despesasMes.stream()
                .collect(Collectors.groupingBy(t -> t.getCategoria().getId()));

        return porCategoria.entrySet().stream()
                .map(entry -> {
                    UUID categoriaId = entry.getKey();
                    List<Transacao> transacoes = entry.getValue();
                    Categoria cat = transacoes.get(0).getCategoria();

                    BigDecimal totalGasto = transacoes.stream()
                            .map(Transacao::getValor)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new GastoPorCategoriaDTO(
                            categoriaId,
                            cat.getNome(),
                            cat.getIcone(),
                            totalGasto,
                            transacoes.size()
                    );
                })
                .sorted(Comparator.comparing(GastoPorCategoriaDTO::totalGasto).reversed())
                .toList();
    }

    /**
     * Regra de Isolamento (Tenant): garante que o usuário autenticado é o dono da transação.
     *
     * @throws UnauthorizedAccessException se a transação pertencer a outro usuário
     */
    private void validarProprietario(Transacao transacao, UUID usuarioAutenticadoId) {
        if (!transacao.getUsuario().getId().equals(usuarioAutenticadoId)) {
            throw new UnauthorizedAccessException("transação");
        }
    }

    private TransacaoResponseDTO toResponseDTO(Transacao transacao) {
        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getUsuario().getId(),
                transacao.getCategoria().getId(),
                transacao.getCategoria().getNome(),
                transacao.getValor(),
                transacao.getTipo().getValor(),
                transacao.getData(),
                transacao.getDescricao()
        );
    }
}
