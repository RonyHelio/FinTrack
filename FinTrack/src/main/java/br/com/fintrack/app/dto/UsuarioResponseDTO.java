package br.com.fintrack.app.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String nome,
        String email,
        LocalDateTime dataCadastro
) {}
