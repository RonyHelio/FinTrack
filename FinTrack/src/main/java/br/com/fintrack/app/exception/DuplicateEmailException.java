package br.com.fintrack.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmailException extends RuntimeException {

    private final String email;

    public DuplicateEmailException(String email) {
        super("E-mail já cadastrado: " + email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
