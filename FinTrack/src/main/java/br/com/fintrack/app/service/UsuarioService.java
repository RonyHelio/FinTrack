package br.com.fintrack.app.service;

import br.com.fintrack.app.dto.UsuarioRequestDTO;
import br.com.fintrack.app.dto.UsuarioResponseDTO;
import br.com.fintrack.app.entity.Usuario;
import br.com.fintrack.app.exception.DuplicateEmailException;
import br.com.fintrack.app.exception.ResourceNotFoundException;
import br.com.fintrack.app.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Responsável pelas operações de CRUD de perfil do usuário.
 * A autenticação (registro/login) é tratada pelo AuthService.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));
        return toResponseDTO(usuario);
    }

    /**
     * Atualiza os dados de perfil do usuário autenticado.
     * Apenas o próprio usuário pode atualizar seu perfil (verificado no controller via JWT).
     */
    @Transactional
    public UsuarioResponseDTO atualizar(UUID id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", id));

        if (!usuario.getEmail().equals(dto.email()) && usuarioRepository.existsByEmail(dto.email())) {
            throw new DuplicateEmailException(dto.email());
        }

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário", "id", id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCadastro()
        );
    }
}
