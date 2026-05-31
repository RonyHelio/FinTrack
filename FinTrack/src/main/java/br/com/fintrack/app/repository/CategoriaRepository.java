package br.com.fintrack.app.repository;

import br.com.fintrack.app.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    List<Categoria> findByUsuarioId(UUID usuarioId);

    List<Categoria> findByUsuarioIsNull();

    List<Categoria> findByUsuarioIdOrUsuarioIsNull(UUID usuarioId);
}
