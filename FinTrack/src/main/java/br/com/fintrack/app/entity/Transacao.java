package br.com.fintrack.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "Tipo é obrigatório")
    @Convert(converter = TipoTransacaoConverter.class)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoTransacao tipo;

    @NotNull(message = "Data é obrigatória")
    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "descricao")
    private String descricao;
}
