package br.com.kollectaops.api.picking.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "filial")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Filial {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String endereco;

    @Column(nullable = false)
    private boolean ativo = true;
}
