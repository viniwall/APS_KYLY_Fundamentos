package br.com.kollectaops.api.picking.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evento_picking")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventoPicking {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id")
    private SessaoColetor sessao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caixa_id", nullable = false)
    private Caixa caixa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_caixa_id")
    private ItemCaixa itemCaixa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peca_id")
    private Peca peca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Tipo tipo;

    @Column(length = 500)
    private String mensagem;

    @Column(name = "ocorrido_em", nullable = false)
    private LocalDateTime ocorridoEm;

    @Column(nullable = false)
    private boolean sincronizado = false;

    public enum Tipo {
        ABRIR_CAIXA, BIPAR_OK, BIPAR_OK_SKU_COMPLETA,
        BIPAR_ERRO_NAO_PERTENCE, BIPAR_ERRO_SEM_SALDO,
        PULAR_ITEM, CONSULTAR_OUTRAS_POSICOES,
        SALVAR_PARCIAL, FINALIZAR_CAIXA, REABRIR_CAIXA
    }
}
