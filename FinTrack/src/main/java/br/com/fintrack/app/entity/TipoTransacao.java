package br.com.fintrack.app.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoTransacao {
    RECEITA("receita"),
    DESPESA("despesa");

    private final String valor;

    TipoTransacao(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    public static TipoTransacao fromValor(String valor) {
        for (TipoTransacao tipo : values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de transação inválido: " + valor + ". Use 'receita' ou 'despesa'.");
    }
}
