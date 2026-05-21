package br.com.kollectaops.api.picking.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sku",
    uniqueConstraints = @UniqueConstraint(columnNames = {"referencia", "cor", "tamanho"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Sku {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String referencia;

    @Column(nullable = false, length = 50)
    private String cor;

    @Column(nullable = false, length = 20)
    private String tamanho;

    @Column(length = 200)
    private String descricao;

    @Column(name = "codigo_ean", length = 30)
    private String codigoEan;

    @Column(nullable = false)
    private boolean ativo = true;
}
