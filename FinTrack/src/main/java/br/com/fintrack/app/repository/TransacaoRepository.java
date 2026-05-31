package br.com.fintrack.app.repository;

import br.com.fintrack.app.entity.Transacao;
import br.com.fintrack.app.entity.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {

    List<Transacao> findByUsuarioId(UUID usuarioId);

    List<Transacao> findByUsuarioIdAndTipo(UUID usuarioId, TipoTransacao tipo);

    List<Transacao> findByUsuarioIdAndDataBetween(UUID usuarioId, LocalDate inicio, LocalDate fim);

    List<Transacao> findByUsuarioIdAndCategoriaId(UUID usuarioId, UUID categoriaId);
}
