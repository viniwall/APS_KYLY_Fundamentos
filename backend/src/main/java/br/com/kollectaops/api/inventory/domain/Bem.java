package br.com.kollectaops.api.inventory.domain;

import br.com.kollectaops.api.picking.domain.Usuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bem")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_patrimonio", nullable = false, unique = true, length = 100)
    private String codigoPatrimonio;

    @Column(length = 300)
    private String descricao;

    @Column(length = 100)
    private String marca;

    @Column(length = 100)
    private String modelo;

    @Column(length = 100)
    private String serial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Situacao situacao = Situacao.ATIVO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "localizacao_atual_id")
    private LocalizacaoPatrimonial localizacaoAtual;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atualizado_por_id")
    private Usuario atualizadoPor;

    public enum Situacao { ATIVO, EM_MANUTENCAO, BAIXADO, NAO_LOCALIZADO }
}
