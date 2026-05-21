package br.com.kollectaops.api.inventory.domain;

import br.com.kollectaops.api.picking.domain.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "leitura_inventario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeituraInventario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventario_id", nullable = false)
    private Inventario inventario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bem_id")
    private Bem bem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "localizacao_lida_id")
    private LocalizacaoPatrimonial localizacaoLida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id")
    private Usuario operador;

    @CreationTimestamp
    @Column(name = "lida_em", nullable = false, updatable = false)
    private LocalDateTime lidaEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Divergencia divergencia = Divergencia.NENHUMA;

    public enum Divergencia { NENHUMA, LOCALIZACAO, SITUACAO, NAO_CADASTRADO }
}
