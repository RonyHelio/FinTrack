package br.com.fintrack.app.repository;

import br.com.fintrack.app.entity.Transacao;
import br.com.fintrack.app.entity.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {

    // ─── Buscas gerais ────────────────────────────────────────────────────────

    List<Transacao> findByUsuarioIdOrderByDataDesc(UUID usuarioId);

    List<Transacao> findByUsuarioIdAndTipo(UUID usuarioId, TipoTransacao tipo);

    List<Transacao> findByUsuarioIdAndDataBetweenOrderByDataDesc(UUID usuarioId, LocalDate inicio, LocalDate fim);

    List<Transacao> findByUsuarioIdAndCategoriaId(UUID usuarioId, UUID categoriaId);

    // Busca com verificação de ownership (tenant isolation)
    Optional<Transacao> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    // ─── Dashboard: aggregações do mês ────────────────────────────────────────

    @Query("""
            SELECT COALESCE(SUM(t.valor), 0)
            FROM Transacao t
            WHERE t.usuario.id = :usuarioId
              AND t.tipo = :tipo
              AND t.data >= :inicio
              AND t.data <= :fim
            """)
    BigDecimal sumValorByUsuarioIdAndTipoAndDataBetween(
            @Param("usuarioId") UUID usuarioId,
            @Param("tipo") TipoTransacao tipo,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    @Query("""
            SELECT COUNT(t)
            FROM Transacao t
            WHERE t.usuario.id = :usuarioId
              AND t.data >= :inicio
              AND t.data <= :fim
            """)
    long countByUsuarioIdAndDataBetween(
            @Param("usuarioId") UUID usuarioId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    // ─── Dashboard: últimas 5 transações ──────────────────────────────────────

    List<Transacao> findTop5ByUsuarioIdOrderByDataDescIdDesc(UUID usuarioId);

    // ─── Dashboard: gastos por categoria (apenas despesas) ────────────────────

    @Query("""
            SELECT t
            FROM Transacao t
            WHERE t.usuario.id = :usuarioId
              AND t.tipo = br.com.fintrack.app.entity.TipoTransacao.DESPESA
              AND t.data >= :inicio
              AND t.data <= :fim
            ORDER BY t.data DESC
            """)
    List<Transacao> findDespesasByUsuarioIdAndDataBetween(
            @Param("usuarioId") UUID usuarioId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    // ─── Filtro combinado (mês/ano + categoria + tipo) ────────────────────────

    /**
     * Filtra COM data e COM tipo.
     */
    @Query("""
            SELECT t FROM Transacao t
            WHERE t.usuario.id = :usuarioId
              AND t.data >= :inicio
              AND t.data <= :fim
              AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId)
              AND t.tipo = :tipo
            ORDER BY t.data DESC
            """)
    List<Transacao> findByFiltrosComDataComTipo(
            @Param("usuarioId") UUID usuarioId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("categoriaId") UUID categoriaId,
            @Param("tipo") TipoTransacao tipo);

    /**
     * Filtra COM data e SEM tipo.
     */
    @Query("""
            SELECT t FROM Transacao t
            WHERE t.usuario.id = :usuarioId
              AND t.data >= :inicio
              AND t.data <= :fim
              AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId)
            ORDER BY t.data DESC
            """)
    List<Transacao> findByFiltrosComDataSemTipo(
            @Param("usuarioId") UUID usuarioId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("categoriaId") UUID categoriaId);

    /**
     * Filtra SEM data e COM tipo.
     */
    @Query("""
            SELECT t FROM Transacao t
            WHERE t.usuario.id = :usuarioId
              AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId)
              AND t.tipo = :tipo
            ORDER BY t.data DESC
            """)
    List<Transacao> findByFiltrosSemDataComTipo(
            @Param("usuarioId") UUID usuarioId,
            @Param("categoriaId") UUID categoriaId,
            @Param("tipo") TipoTransacao tipo);

    /**
     * Filtra SEM data e SEM tipo (apenas categoria opcional).
     */
    @Query("""
            SELECT t FROM Transacao t
            WHERE t.usuario.id = :usuarioId
              AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId)
            ORDER BY t.data DESC
            """)
    List<Transacao> findByFiltrosSemDataSemTipo(
            @Param("usuarioId") UUID usuarioId,
            @Param("categoriaId") UUID categoriaId);

    // ─── Legados mantidos por compatibilidade ─────────────────────────────────

    default List<Transacao> findByUsuarioId(UUID usuarioId) {
        return findByUsuarioIdOrderByDataDesc(usuarioId);
    }

    default List<Transacao> findByUsuarioIdAndDataBetween(UUID usuarioId, LocalDate inicio, LocalDate fim) {
        return findByUsuarioIdAndDataBetweenOrderByDataDesc(usuarioId, inicio, fim);
    }
}
