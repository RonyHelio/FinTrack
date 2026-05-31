package br.com.fintrack.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidLoginException extends RuntimeException {

    public InvalidLoginException() {
        super("E-mail ou senha inválidos.");
    }

    public InvalidLoginException(String message) {
        super(message);
    }
}
