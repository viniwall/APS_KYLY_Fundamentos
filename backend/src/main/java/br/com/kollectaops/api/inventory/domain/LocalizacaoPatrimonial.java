package br.com.kollectaops.api.inventory.domain;

import br.com.kollectaops.api.picking.domain.Filial;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "localizacao_patrimonial")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LocalizacaoPatrimonial {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filial_id")
    private Filial filial;

    @Column(nullable = false)
    private boolean ativo = true;
}
