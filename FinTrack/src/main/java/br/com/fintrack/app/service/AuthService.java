package br.com.fintrack.app.service;

import br.com.fintrack.app.dto.LoginRequestDTO;
import br.com.fintrack.app.dto.LoginResponseDTO;
import br.com.fintrack.app.dto.UsuarioRequestDTO;
import br.com.fintrack.app.dto.UsuarioResponseDTO;
import br.com.fintrack.app.entity.Usuario;
import br.com.fintrack.app.exception.DuplicateEmailException;
import br.com.fintrack.app.exception.InvalidLoginException;
import br.com.fintrack.app.repository.UsuarioRepository;
import br.com.fintrack.app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsável por registro e autenticação de usuários.
 * Centraliza toda a lógica de auth, mantendo o UsuarioService focado
 * em operações CRUD de perfil.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    /**
     * Registra um novo usuário e retorna seus dados (sem token).
     * O login deve ser feito em seguida para obter o JWT.
     */
    @Transactional
    public UsuarioResponseDTO registrar(UsuarioRequestDTO dto) {
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

    /**
     * Autentica um usuário com email e senha.
     * Retorna os dados do usuário junto com o JWT gerado.
     *
     * @throws InvalidLoginException se o email não for encontrado ou a senha for incorreta
     */
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

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCadastro()
        );
    }
}
