package br.com.kollectaops.api.picking.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_caixa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemCaixa {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_id", nullable = false)
    private Caixa caixa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "endereco_sugerido_id")
    private EnderecoEstoque enderecoSugerido;

    @Column(name = "qtde_solicitada", nullable = false)
    private int qtdeSolicitada;

    @Column(name = "qtde_coletada", nullable = false)
    private int qtdeColetada = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDENTE;

    @Column(name = "ordem_picking", nullable = false)
    private int ordemPicking;

    public enum Status { PENDENTE, EM_COLETA, COMPLETO, EM_FALTA, DIVERGENTE }
}
