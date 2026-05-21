package br.com.kollectaops.api.picking.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "caixa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Caixa {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_papeleta", nullable = false, unique = true, length = 50)
    private String codigoPapeleta;

    @Column(name = "numero_op", length = 50)
    private String numeroOp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "tipo_caixa", length = 50)
    private String tipoCaixa;

    @Column(name = "peso_bruto", precision = 8, scale = 3)
    private BigDecimal pesoBruto;

    @Enumerated(EnumType.STRING)
    @Column(name = "cor_tarja", nullable = false, length = 20)
    private CorTarja corTarja = CorTarja.PADRAO;

    private Integer sequencia;

    @Column(name = "total_caixas_pedido")
    private Integer totalCaixasPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.AGUARDANDO;

    @Column(name = "aberta_em")
    private LocalDateTime abertaEm;

    @Column(name = "finalizada_em")
    private LocalDateTime finalizadaEm;

    @OneToMany(mappedBy = "caixa", fetch = FetchType.LAZY)
    @OrderBy("ordemPicking ASC")
    private List<ItemCaixa> itens;

    public enum CorTarja { PADRAO, EXPORTACAO_AZUL, TAG_VERDE, TAG_ROSA, MULTI_AMARELO, VERMELHA }
    public enum Status { AGUARDANDO, EM_PICKING, PARCIAL, FINALIZADA, CANCELADA }
}
