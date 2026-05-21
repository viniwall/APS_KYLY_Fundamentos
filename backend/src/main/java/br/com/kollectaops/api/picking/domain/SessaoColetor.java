package br.com.kollectaops.api.picking.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessao_coletor")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SessaoColetor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Usuario supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id", nullable = false)
    private Usuario operador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id")
    private Turno turno;

    @Column(name = "coletor_serial", length = 100)
    private String coletorSerial;

    @CreationTimestamp
    @Column(name = "aberta_em", nullable = false, updatable = false)
    private LocalDateTime abertaEm;

    @Column(name = "encerrada_em")
    private LocalDateTime encerradaEm;

    @Column(name = "motivo_encerramento", length = 255)
    private String motivoEncerramento;
}
