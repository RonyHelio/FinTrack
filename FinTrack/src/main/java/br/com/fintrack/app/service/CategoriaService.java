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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Cria uma categoria personalizada vinculada ao usuário autenticado.
     * O usuarioId vem do JWT, não do body da requisição.
     */
    @Transactional
    public CategoriaResponseDTO criar(UUID usuarioAutenticadoId, CategoriaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioAutenticadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", usuarioAutenticadoId));

        Categoria categoria = Categoria.builder()
                .usuario(usuario)
                .nome(dto.nome())
                .icone(dto.icone())
                .build();

        return toResponseDTO(categoriaRepository.save(categoria));
    }

    /**
     * Lista as categorias visíveis para o usuário: as suas + as globais (usuario = null).
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarPorUsuario(UUID usuarioId) {
        return categoriaRepository.findByUsuarioIdOrUsuarioIsNull(usuarioId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Lista apenas as categorias globais (usuario_id = null).
     */
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarGlobais() {
        return categoriaRepository.findByUsuarioIsNull()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(UUID id) {
        return toResponseDTO(categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id)));
    }

    /**
     * Atualiza uma categoria personalizada.
     * Garante que o usuário autenticado é o dono.
     * Categorias globais (usuario = null) não podem ser editadas.
     *
     * @throws UnauthorizedAccessException se tentar editar categoria de outro usuário
     * @throws BusinessException se tentar editar uma categoria global
     */
    @Transactional
    public CategoriaResponseDTO atualizar(UUID id, UUID usuarioAutenticadoId, CategoriaRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

        validarProprietario(categoria, usuarioAutenticadoId);

        categoria.setNome(dto.nome());
        categoria.setIcone(dto.icone());

        return toResponseDTO(categoriaRepository.save(categoria));
    }

    /**
     * Exclui uma categoria personalizada.
     * Garante que o usuário autenticado é o dono.
     * Categorias globais (usuario = null) não podem ser excluídas.
     *
     * @throws UnauthorizedAccessException se tentar excluir categoria de outro usuário
     * @throws BusinessException se tentar excluir uma categoria global
     */
    @Transactional
    public void deletar(UUID id, UUID usuarioAutenticadoId) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

        validarProprietario(categoria, usuarioAutenticadoId);

        categoriaRepository.delete(categoria);
    }

    // ─── Helpers privados ─────────────────────────────────────────────────────

    /**
     * Garante que o usuário autenticado é o dono da categoria.
     * Categorias globais (usuario = null) são imutáveis por qualquer usuário.
     */
    private void validarProprietario(Categoria categoria, UUID usuarioAutenticadoId) {
        if (categoria.getUsuario() == null) {
            throw new BusinessException("Categorias globais não podem ser modificadas.");
        }
        if (!categoria.getUsuario().getId().equals(usuarioAutenticadoId)) {
            throw new UnauthorizedAccessException("categoria");
        }
    }

    private CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getUsuario() != null ? categoria.getUsuario().getId() : null,
                categoria.getNome(),
                categoria.getIcone()
        );
    }
}
