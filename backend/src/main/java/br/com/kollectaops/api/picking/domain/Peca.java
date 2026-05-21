package br.com.kollectaops.api.picking.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "peca")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Peca {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_unico", nullable = false, unique = true, length = 100)
    private String codigoUnico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.DISPONIVEL;

    @Column(name = "bipada_em")
    private LocalDateTime bipadaEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bipada_por_id")
    private Usuario bipadaPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_caixa_id")
    private ItemCaixa itemCaixa;

    public enum Status { DISPONIVEL, BIPADA, EM_CAIXA, DIVERGENTE }
}
