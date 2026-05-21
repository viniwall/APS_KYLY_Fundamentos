package br.com.kollectaops.api.inventory.domain;

import br.com.kollectaops.api.picking.domain.Filial;
import br.com.kollectaops.api.picking.domain.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inventario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "filial_id", nullable = false)
    private Filial filial;

    @Column(length = 200)
    private String descricao;

    @CreationTimestamp
    @Column(name = "iniciado_em", nullable = false, updatable = false)
    private LocalDateTime iniciadoEm;

    @Column(name = "finalizado_em")
    private LocalDateTime finalizadoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ABERTO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por_id")
    private Usuario criadoPor;

    public enum Status { ABERTO, EM_ANDAMENTO, FECHADO }
}
