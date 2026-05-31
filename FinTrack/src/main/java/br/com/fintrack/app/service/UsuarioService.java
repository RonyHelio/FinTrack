package br.com.fintrack.app.service;

import br.com.fintrack.app.dto.LoginRequestDTO;
import br.com.fintrack.app.dto.LoginResponseDTO;
import br.com.fintrack.app.dto.UsuarioRequestDTO;
import br.com.fintrack.app.dto.UsuarioResponseDTO;
import br.com.fintrack.app.entity.Usuario;
import br.com.fintrack.app.exception.DuplicateEmailException;
import br.com.fintrack.app.exception.InvalidLoginException;
import br.com.fintrack.app.exception.ResourceNotFoundException;
import br.com.fintrack.app.repository.UsuarioRepository;
import br.com.fintrack.app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new DuplicateEmailException(dto.email());
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(dto.senha())
                .build();

        Usuario salvo = usuarioRepository.save(usuario);
        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(InvalidLoginException::new);

        if (!usuario.getSenha().equals(dto.senha())) {
            throw new InvalidLoginException();
        }

        String token = jwtService.generateToken(usuario.getId(), usuario.getEmail());

        return new LoginResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                token,
                "Login realizado com sucesso"
        );
    }

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

        Usuario atualizado = usuarioRepository.save(usuario);
        return toResponseDTO(atualizado);
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
