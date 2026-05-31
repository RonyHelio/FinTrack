package br.com.fintrack.app.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoTransacaoConverter implements AttributeConverter<TipoTransacao, String> {

    @Override
    public String convertToDatabaseColumn(TipoTransacao tipo) {
        return tipo == null ? null : tipo.getValor();
    }

    @Override
    public TipoTransacao convertToEntityAttribute(String valor) {
        return valor == null ? null : TipoTransacao.fromValor(valor);
    }
}
