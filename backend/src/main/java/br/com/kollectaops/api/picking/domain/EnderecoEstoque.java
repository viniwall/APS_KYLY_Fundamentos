package br.com.kollectaops.api.picking.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "endereco_estoque")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EnderecoEstoque {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(name = "andar_rua", length = 10)
    private String andarRua;

    @Column(length = 10)
    private String secao;

    @Column(name = "posicao_nivel", length = 10)
    private String posicaoNivel;

    @Column(nullable = false)
    private boolean ativo = true;
}
