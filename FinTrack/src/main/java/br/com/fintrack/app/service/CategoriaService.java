package br.com.fintrack.app.service;

import br.com.fintrack.app.dto.CategoriaRequestDTO;
import br.com.fintrack.app.dto.CategoriaResponseDTO;
import br.com.fintrack.app.entity.Categoria;
import br.com.fintrack.app.entity.Usuario;
import br.com.fintrack.app.exception.ResourceNotFoundException;
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

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        Usuario usuario = null;
        if (dto.usuarioId() != null) {
            usuario = usuarioRepository.findById(dto.usuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", dto.usuarioId()));
        }

        Categoria categoria = Categoria.builder()
                .usuario(usuario)
                .nome(dto.nome())
                .icone(dto.icone())
                .build();

        Categoria salva = categoriaRepository.save(categoria);
        return toResponseDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarPorUsuario(UUID usuarioId) {
        return categoriaRepository.findByUsuarioIdOrUsuarioIsNull(usuarioId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarGlobais() {
        return categoriaRepository.findByUsuarioIsNull()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(UUID id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
        return toResponseDTO(categoria);
    }

    @Transactional
    public CategoriaResponseDTO atualizar(UUID id, CategoriaRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

        Usuario usuario = null;
        if (dto.usuarioId() != null) {
            usuario = usuarioRepository.findById(dto.usuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", dto.usuarioId()));
        }

        categoria.setUsuario(usuario);
        categoria.setNome(dto.nome());
        categoria.setIcone(dto.icone());

        Categoria atualizada = categoriaRepository.save(categoria);
        return toResponseDTO(atualizada);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria", "id", id);
        }
        categoriaRepository.deleteById(id);
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
