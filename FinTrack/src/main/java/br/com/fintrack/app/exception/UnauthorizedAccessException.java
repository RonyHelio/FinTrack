package br.com.fintrack.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException() {
        super("Acesso negado: você não tem permissão para acessar este recurso.");
    }

    public UnauthorizedAccessException(String recurso) {
        super("Acesso negado: você não tem permissão para acessar este(a) " + recurso + ".");
    }
}
