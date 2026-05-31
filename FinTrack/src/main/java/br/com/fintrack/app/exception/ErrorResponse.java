package br.com.fintrack.app.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final int status;
    private final String mensagem;
    private final LocalDateTime timestamp;
    private final List<String> erros;

    public ErrorResponse(int status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
        this.erros = null;
    }
}
